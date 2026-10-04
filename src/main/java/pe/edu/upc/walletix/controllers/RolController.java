package pe.edu.upc.walletix.controllers;

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

// Solo un ADMIN puede ver y asignar roles
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
        return rolDTO;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RolDTO>> listar() {
        List<RolDTO> listaRoles = rolService.list().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaRoles);
    }

    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody RolDTO rolDTO) {
        String nombreRol = rolDTO.getRol() == null ? "" : rolDTO.getRol().toUpperCase();
        if (!nombreRol.equals("USUARIO") && !nombreRol.equals("ADMIN")) {
            return ResponseEntity.badRequest().body("El rol debe ser USUARIO o ADMIN");
        }
        Optional<Usuario> usuario = usuarioService.listId(rolDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + rolDTO.getIdUsuario() + " no existe.");
        }
        if (rolService.existeRol(rolDTO.getIdUsuario(), nombreRol)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya tiene el rol " + nombreRol);
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setRol(nombreRol);
        nuevoRol.setUsuario(usuario.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(rolService.insert(nuevoRol)));
    }
}
