package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.CambioContrasenaDTO;
import pe.edu.upc.walletix.dtos.UsuarioDTO;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Usuarios", description = "Registro y perfil de los usuarios")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    // Validaciones comunes del registro y de la edición del perfil
    private String validar(UsuarioDTO dto) {
        if (estaVacio(dto.getNombreUsuario())) {
            return "El nombre es obligatorio";
        }
        if (estaVacio(dto.getCorreoUsuario()) || !dto.getCorreoUsuario().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "El correo no es válido";
        }
        // El correo se guarda siempre en minúsculas para que el login no dependa de mayúsculas
        dto.setCorreoUsuario(dto.getCorreoUsuario().trim().toLowerCase());
        if (dto.getFechaNacimientoUsuario() == null) {
            return "La fecha de nacimiento es obligatoria";
        }
        if (dto.getFechaNacimientoUsuario().isAfter(LocalDate.now())) {
            return "La fecha de nacimiento no puede ser futura";
        }
        if (String.valueOf(dto.getTelefonoUsuario()).length() != 9) {
            return "El teléfono debe tener exactamente 9 dígitos";
        }
        if (estaVacio(dto.getSegmentoUsuario())) {
            return "El segmento es obligatorio";
        }
        if (dto.getSaldoActualUsuario() == null || dto.getSaldoActualUsuario().compareTo(BigDecimal.ZERO) < 0) {
            return "El saldo es obligatorio y no puede ser negativo";
        }
        if (dto.getPuntosGamificacionUsuario() < 0) {
            return "Los puntos de gamificación no pueden ser negativos";
        }
        return null;
    }

    private boolean esAdmin(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ADMIN"));
    }

    // Un USUARIO solo puede trabajar con su propia cuenta; un ADMIN con cualquiera
    private boolean puedeGestionar(Authentication autenticacion, int idUsuario) {
        if (esAdmin(autenticacion)) {
            return true;
        }
        Optional<Usuario> usuarioActual = usuarioService.buscarPorCorreo(autenticacion.getName());
        return usuarioActual.isPresent() && usuarioActual.get().getIdUsuario() == idUsuario;
    }

    @Operation(summary = "Listar todos los usuarios activos (solo ADMIN)")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listar(){
        ModelMapper m= new ModelMapper();
        List<UsuarioDTO>listaUsuarios= usuarioService.list().stream()
                .map(y->m.map(y, UsuarioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaUsuarios);
    }
    @Operation(summary = "Registrar un usuario nuevo (libre, recibe el rol USUARIO)")
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDTO dto){
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (dto.getContrasenaUsuario() == null || dto.getContrasenaUsuario().length() < 6) {
            return ResponseEntity.badRequest()
                    .body("La contraseña debe tener al menos 6 caracteres");
        }
        // El correo es el usuario del login, por eso no se puede repetir
        if (usuarioService.existeCorreo(dto.getCorreoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo ya está registrado");
        }

        ModelMapper m=new ModelMapper();
        Usuario c=m.map(dto, Usuario.class);
        c.setEstadoUsuario(1); // Siempre nace en 1 al registrar
        c.setPuntosGamificacionUsuario(0); // Los puntos los otorga el sistema, no se eligen al registrarse
        // La contraseña se guarda cifrada con BCrypt, nunca en texto plano
        c.setContrasenaUsuario(passwordEncoder.encode(dto.getContrasenaUsuario()));
        Usuario cur= usuarioService.insert(c);
        // Todo usuario nuevo recibe el rol USUARIO (relación usuario-rol de 1 a muchos)
        Rol rolUsuario = new Rol();
        rolUsuario.setRol("USUARIO");
        rolUsuario.setUsuario(cur);
        rolService.insert(rolUsuario);
        UsuarioDTO responseDTO=m.map(cur, UsuarioDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @Operation(summary = "Buscar un usuario por su id (un USUARIO solo puede ver su propia cuenta)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo puede ver su propia cuenta");
        }
        ModelMapper m = new ModelMapper();
        Optional<Usuario> mach = usuarioService.listId(id);
        if (mach.isPresent()) {
            UsuarioDTO dto = m.map(mach.get(), UsuarioDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }
    @Operation(summary = "Actualizar el perfil de un usuario (un USUARIO solo el suyo; los puntos solo los cambia un ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDTO dto, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, dto.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo puede editar su propia cuenta");
        }
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Usuario> existente = usuarioService.listId(dto.getIdUsuario());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Usuario us = existente.get();
        // Si cambia el correo, el nuevo no puede pertenecer a otra cuenta
        if (!us.getCorreoUsuario().equalsIgnoreCase(dto.getCorreoUsuario()) && usuarioService.existeCorreo(dto.getCorreoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El correo ya está registrado");
        }
        us.setNombreUsuario(dto.getNombreUsuario());
        us.setCorreoUsuario(dto.getCorreoUsuario());
        us.setTelefonoUsuario(dto.getTelefonoUsuario());
        us.setFechaNacimientoUsuario(dto.getFechaNacimientoUsuario());
        us.setSegmentoUsuario(dto.getSegmentoUsuario());
        us.setSaldoActualUsuario(dto.getSaldoActualUsuario());
        // Los puntos de gamificación los otorga el sistema: un USUARIO no puede cambiarse los suyos
        if (esAdmin(autenticacion)) {
            us.setPuntosGamificacionUsuario(dto.getPuntosGamificacionUsuario());
        }
        usuarioService.update(us);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }
    @Operation(summary = "Cambiar la contraseña de la cuenta con la que se inició sesión")
    @PutMapping("/contrasena")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> cambiarContrasena(@RequestBody CambioContrasenaDTO dto, Authentication autenticacion) {
        Optional<Usuario> usuarioActual = usuarioService.buscarPorCorreo(autenticacion.getName());
        if (usuarioActual.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        Usuario us = usuarioActual.get();
        if (dto.getContrasenaActual() == null || !passwordEncoder.matches(dto.getContrasenaActual(), us.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().body("La contraseña actual es incorrecta");
        }
        if (dto.getContrasenaNueva() == null || dto.getContrasenaNueva().length() < 6) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe tener al menos 6 caracteres");
        }
        if (passwordEncoder.matches(dto.getContrasenaNueva(), us.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe ser distinta de la actual");
        }
        us.setContrasenaUsuario(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuarioService.update(us);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
    @Operation(summary = "Eliminar un usuario (borrado lógico; un USUARIO solo puede eliminar su propia cuenta)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo puede eliminar su propia cuenta");
        }
        Optional<Usuario> usuario = usuarioService.listId(id);
        if (usuario.isPresent()) {
            // Evita que el sistema se quede sin ningún ADMIN activo
            boolean esAdministrador = rolService.listarActivosDeUsuario(id).stream()
                    .anyMatch(rol -> rol.getRol().equals("ADMIN"));
            if (esAdministrador && rolService.contarUsuariosActivosConRol("ADMIN") <= 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar al único administrador del sistema");
            }
            usuarioService.delete(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
    }
}
