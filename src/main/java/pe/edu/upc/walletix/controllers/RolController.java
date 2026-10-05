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

// Solo un ADMIN puede ver, asignar, cambiar y quitar roles
@Tag(name = "Roles", description = "Roles de seguridad de los usuarios (solo ADMIN)")
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

    // Evita que el sistema se quede sin ningún ADMIN activo
    private boolean esUltimoAdmin(Rol rol) {
        return rol.getRol().equals("ADMIN") && rolService.contarUsuariosActivosConRol("ADMIN") <= 1;
    }

    @Operation(summary = "Listar los roles activos de los usuarios")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RolDTO>> listar() {
        List<RolDTO> listaRoles = rolService.list().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaRoles);
    }

    @Operation(summary = "Asignar un rol (USUARIO o ADMIN) a un usuario")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody RolDTO rolDTO) {
        String nombreRol = normalizarRol(rolDTO.getRol());
        if (nombreRol == null) {
            return ResponseEntity.badRequest().body("El rol debe ser USUARIO o ADMIN");
        }
        Optional<Usuario> usuario = usuarioService.listId(rolDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + rolDTO.getIdUsuario() + " no existe.");
        }
        Optional<Rol> rolExistente = rolService.buscarPorUsuarioYRol(rolDTO.getIdUsuario(), nombreRol);
        if (rolExistente.isPresent() && rolExistente.get().getEstadoRol() == 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya tiene el rol " + nombreRol);
        }
        if (rolExistente.isPresent()) {
            // El rol se había eliminado antes: se reactiva la misma fila en vez de crear una repetida
            Rol rolReactivado = rolExistente.get();
            rolReactivado.setEstadoRol(1);
            rolService.update(rolReactivado);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(rolReactivado));
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setRol(nombreRol);
        nuevoRol.setUsuario(usuario.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(rolService.insert(nuevoRol)));
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

    @Operation(summary = "Cambiar el rol de un registro (ej. de ADMIN a USUARIO)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody RolDTO rolDTO) {
        Optional<Rol> rolExistente = rolService.listId(rolDTO.getIdRol());
        if (rolExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
        String nuevoRol = normalizarRol(rolDTO.getRol());
        if (nuevoRol == null) {
            return ResponseEntity.badRequest().body("El rol debe ser USUARIO o ADMIN");
        }
        Rol rol = rolExistente.get();
        int idUsuario = rol.getUsuario().getIdUsuario();
        // No se permite que el usuario quede con el mismo rol repetido
        Optional<Rol> rolRepetido = rolService.buscarPorUsuarioYRol(idUsuario, nuevoRol);
        if (rolRepetido.isPresent() && rolRepetido.get().getEstadoRol() == 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El usuario ya tiene el rol " + nuevoRol + ". Para quitarle un rol use DELETE /roles/{id}");
        }
        if (esUltimoAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede quitar el rol ADMIN al único administrador del sistema");
        }
        if (rolRepetido.isPresent()) {
            // El nuevo rol existía eliminado: se reactiva esa fila y se da de baja la actual
            Rol rolReactivado = rolRepetido.get();
            rolReactivado.setEstadoRol(1);
            rolService.update(rolReactivado);
            rolService.delete(rol.getIdRol());
        } else {
            rol.setRol(nuevoRol);
            rolService.update(rol);
        }
        return ResponseEntity.ok("Rol actualizado correctamente");
    }

    @Operation(summary = "Quitar un rol a un usuario (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Rol> rolExistente = rolService.listId(id);
        if (rolExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Rol no encontrado");
        }
        Rol rol = rolExistente.get();
        if (rolService.contarRolesActivosDeUsuario(rol.getUsuario().getIdUsuario()) <= 1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar el único rol del usuario; cámbielo con PUT /roles/actualiza");
        }
        if (esUltimoAdmin(rol)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede quitar el rol ADMIN al único administrador del sistema");
        }
        rolService.delete(id);
        return ResponseEntity.ok("Rol eliminado correctamente");
    }
}
