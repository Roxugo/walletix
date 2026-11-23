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
import pe.edu.upc.walletix.i18n.Mensajes;
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
    private Mensajes mensajes;
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
            return mensajes.get("nombre.obligatorio");
        }
        if (estaVacio(dto.getCorreoUsuario()) || !dto.getCorreoUsuario().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return mensajes.get("correo.no.valido");
        }
        // El correo se guarda siempre en minúsculas para que el login no dependa de mayúsculas
        dto.setCorreoUsuario(dto.getCorreoUsuario().trim().toLowerCase());
        if (dto.getFechaNacimientoUsuario() == null) {
            return mensajes.get("fecha.nacimiento.obligatoria");
        }
        if (dto.getFechaNacimientoUsuario().isAfter(LocalDate.now())) {
            return mensajes.get("fecha.nacimiento.no.puede.ser");
        }
        if (String.valueOf(dto.getTelefonoUsuario()).length() != 9) {
            return mensajes.get("telefono.debe.tener.exactamente.9");
        }
        if (estaVacio(dto.getSegmentoUsuario())) {
            return mensajes.get("segmento.obligatorio");
        }
        if (dto.getSaldoActualUsuario() == null || dto.getSaldoActualUsuario().compareTo(BigDecimal.ZERO) < 0) {
            return mensajes.get("saldo.obligatorio.no.puede.ser");
        }
        if (dto.getPuntosGamificacionUsuario() < 0) {
            return mensajes.get("puntos.gamificacion.no.pueden.ser");
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
                    .body(mensajes.get("contrasena.debe.tener.menos.6"));
        }
        // El correo es el usuario del login, por eso no se puede repetir
        if (usuarioService.existeCorreo(dto.getCorreoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensajes.get("correo.esta.registrado"));
        }
        if (usuarioService.existeTelefono(dto.getTelefonoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensajes.get("telefono.esta.registrado"));
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
    @Operation(summary = "Datos de la cuenta con la que se inició sesión (los saca del token)")
    @GetMapping("/yo")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> miCuenta(Authentication autenticacion) {
        // El subject del token es el correo, por eso se busca por correo
        Optional<Usuario> usuario = usuarioService.buscarPorCorreo(autenticacion.getName());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        }
        ModelMapper m = new ModelMapper();
        return ResponseEntity.ok(m.map(usuario.get(), UsuarioDTO.class));
    }
    @Operation(summary = "Buscar un usuario por su id (un USUARIO solo puede ver su propia cuenta)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("solo.puede.ver.propia.cuenta"));
        }
        ModelMapper m = new ModelMapper();
        Optional<Usuario> mach = usuarioService.listId(id);
        if (mach.isPresent()) {
            UsuarioDTO dto = m.map(mach.get(), UsuarioDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("usuario.no.encontrado"));
        }
    }
    @Operation(summary = "Actualizar el perfil de un usuario (un USUARIO solo el suyo; los puntos solo los cambia un ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDTO dto, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, dto.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("solo.puede.editar.propia.cuenta"));
        }
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Usuario> existente = usuarioService.listId(dto.getIdUsuario());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("usuario.no.encontrado"));
        }
        Usuario us = existente.get();
        // Si cambia el correo, el nuevo no puede pertenecer a otra cuenta
        if (!us.getCorreoUsuario().equalsIgnoreCase(dto.getCorreoUsuario()) && usuarioService.existeCorreo(dto.getCorreoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensajes.get("correo.esta.registrado"));
        }
        // Si cambia el teléfono, el nuevo no puede pertenecer a otra cuenta
        if (us.getTelefonoUsuario() != dto.getTelefonoUsuario() && usuarioService.existeTelefono(dto.getTelefonoUsuario())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mensajes.get("telefono.esta.registrado"));
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
        return ResponseEntity.ok(mensajes.get("usuario.actualizado.correctamente"));
    }
    @Operation(summary = "Cambiar la contraseña de la cuenta con la que se inició sesión")
    @PutMapping("/contrasena")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> cambiarContrasena(@RequestBody CambioContrasenaDTO dto, Authentication autenticacion) {
        Optional<Usuario> usuarioActual = usuarioService.buscarPorCorreo(autenticacion.getName());
        if (usuarioActual.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        }
        Usuario us = usuarioActual.get();
        if (dto.getContrasenaActual() == null || !passwordEncoder.matches(dto.getContrasenaActual(), us.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().body(mensajes.get("contrasena.actual.incorrecta"));
        }
        if (dto.getContrasenaNueva() == null || dto.getContrasenaNueva().length() < 6) {
            return ResponseEntity.badRequest().body(mensajes.get("nueva.contrasena.debe.tener.menos"));
        }
        if (passwordEncoder.matches(dto.getContrasenaNueva(), us.getContrasenaUsuario())) {
            return ResponseEntity.badRequest().body(mensajes.get("nueva.contrasena.debe.ser.distinta"));
        }
        us.setContrasenaUsuario(passwordEncoder.encode(dto.getContrasenaNueva()));
        usuarioService.update(us);
        return ResponseEntity.ok(mensajes.get("contrasena.actualizada.correctamente"));
    }
    @Operation(summary = "Eliminar un usuario (borrado lógico; un USUARIO solo puede eliminar su propia cuenta)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        if (!puedeGestionar(autenticacion, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("solo.puede.eliminar.propia.cuenta"));
        }
        Optional<Usuario> usuario = usuarioService.listId(id);
        if (usuario.isPresent()) {
            // Evita que el sistema se quede sin ningún ADMIN activo
            boolean esAdministrador = rolService.listarActivosDeUsuario(id).stream()
                    .anyMatch(rol -> rol.getRol().equals("ADMIN"));
            if (esAdministrador && rolService.contarUsuariosActivosConRol("ADMIN") <= 1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(mensajes.get("no.puede.eliminar.unico.administrador"));
            }
            usuarioService.delete(id);
            return ResponseEntity.ok(mensajes.get("usuario.eliminado.correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("usuario.no.encontrado"));
        }
    }
}
