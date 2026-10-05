package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.RolDTO;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Cada usuario tiene un solo rol: nace como USUARIO al registrarse y un ADMIN puede cambiarlo.
// No hay POST ni DELETE: un usuario nunca queda sin rol ni con dos roles
@Tag(name = "Roles", description = "Rol de cada usuario (USUARIO o ADMIN). Solo un ADMIN puede verlos y cambiarlos")
@RestController
@RequestMapping("/roles")
public class RolController {
    @Autowired
    private IRolService rolService;
    @Autowired
    private IUsuarioService usuarioService;

    private RolDTO convertirADTO(Rol rol) {
        RolDTO rolDTO = new RolDTO();
        rolDTO.setIdRol(rol.getIdRol());
        rolDTO.setRol(rol.getRol());
        rolDTO.setIdUsuario(rol.getUsuario().getIdUsuario());
        rolDTO.setEstadoRol(rol.getEstadoRol());
        return rolDTO;
    }

    // Devuelve el rol en mayúsculas, o null si no es USUARIO ni ADMIN
    private String normalizarRol(String rol) {
        String nombreRol = rol == null ? "" : rol.trim().toUpperCase();
        return nombreRol.equals("USUARIO") || nombreRol.equals("ADMIN") ? nombreRol : null;
    }

    @Operation(summary = "Listar el rol de cada usuario")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RolDTO>> listar() {
        List<RolDTO> listaRoles = rolService.list().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaRoles);
    }

    @Operation(summary = "Buscar un rol por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Rol> rol = rolService.listId(id);
        if (rol.isPresent()) {
            return ResponseEntity.ok(convertirADTO(rol.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
    }

    @Operation(summary = "Cambiar el rol de un usuario (USUARIO o ADMIN). Solo se envía idUsuario y rol")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody RolDTO rolDTO) {
        String nuevoRol = normalizarRol(rolDTO.getRol());
        if (nuevoRol == null) {
            return ResponseEntity.badRequest().body("El rol debe ser USUARIO o ADMIN");
        }
        Optional<Usuario> usuario = usuarioService.listId(rolDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + rolDTO.getIdUsuario() + " no existe.");
        }
        int idUsuario = rolDTO.getIdUsuario();
        List<Rol> rolesActuales = rolService.listarActivosDeUsuario(idUsuario);
        if (rolesActuales.size() == 1 && rolesActuales.get(0).getRol().equals(nuevoRol)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya tiene el rol " + nuevoRol);
        }
        // Evita que el sistema se quede sin ningún ADMIN activo
        boolean eraAdmin = rolesActuales.stream().anyMatch(rol -> rol.getRol().equals("ADMIN"));
        if (eraAdmin && nuevoRol.equals("USUARIO") && rolService.contarUsuariosActivosConRol("ADMIN") <= 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede quitar el rol ADMIN al único administrador del sistema");
        }
        // Se activa el nuevo rol (reutilizando su fila si ya existía dada de baja, para no duplicarla)
        Optional<Rol> filaNuevoRol = rolService.buscarPorUsuarioYRol(idUsuario, nuevoRol);
        if (filaNuevoRol.isPresent()) {
            filaNuevoRol.get().setEstadoRol(1);
            rolService.update(filaNuevoRol.get());
        } else {
            Rol rol = new Rol();
            rol.setRol(nuevoRol);
            rol.setUsuario(usuario.get());
            rolService.insert(rol);
        }
        // Y se da de baja el rol anterior: el usuario siempre queda con un solo rol
        for (Rol rolAnterior : rolesActuales) {
            if (!rolAnterior.getRol().equals(nuevoRol)) {
                rolAnterior.setEstadoRol(0);
                rolService.update(rolAnterior);
            }
        }
        return ResponseEntity.ok("Rol actualizado: el usuario ahora es " + nuevoRol);
    }
}
