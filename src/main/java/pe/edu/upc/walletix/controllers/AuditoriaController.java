package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.AuditoriaDTO;
import pe.edu.upc.walletix.entities.Auditoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IAuditoriaService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    @Autowired
    private IAuditoriaService auditoriaService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<AuditoriaDTO>> listar() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<AuditoriaDTO> lista = auditoriaService.list().stream()
                .map(y -> {
                    AuditoriaDTO dto = m.map(y, AuditoriaDTO.class);
                    if (y.getUsuarioRegistro() != null) {
                        dto.setIdUsuarioRegistro(y.getUsuarioRegistro().getIdUsuario());
                    }
                    if (y.getUsuarioEditar() != null) {
                        dto.setIdUsuarioEditar(y.getUsuarioEditar().getIdUsuario());
                    }
                    if (y.getUsuarioEliminar() != null) {
                        dto.setIdUsuarioEliminar(y.getUsuarioEliminar().getIdUsuario());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody AuditoriaDTO dto) {
        // Validar que el usuario que registra exista
        Optional<Usuario> usuarioRegistroOpt = usuarioService.listId(dto.getIdUsuarioRegistro());
        if (usuarioRegistroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + dto.getIdUsuarioRegistro() + " no existe.");
        }

        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Auditoria auditoria = m.map(dto, Auditoria.class);

        auditoria.setUsuarioRegistro(usuarioRegistroOpt.get());
        if (auditoria.getFechaRegistro() == null) {
            auditoria.setFechaRegistro(LocalDateTime.now());
        }
        auditoria.setEstado(true);

        if (dto.getIdUsuarioEditar() != null) {
            Optional<Usuario> editorOpt = usuarioService.listId(dto.getIdUsuarioEditar());
            editorOpt.ifPresent(auditoria::setUsuarioEditar);
        }

        if (dto.getIdUsuarioEliminar() != null) {
            Optional<Usuario> eliminadorOpt = usuarioService.listId(dto.getIdUsuarioEliminar());
            eliminadorOpt.ifPresent(auditoria::setUsuarioEliminar);
        }

        Auditoria guardada = auditoriaService.insert(auditoria);
        AuditoriaDTO responseDTO = m.map(guardada, AuditoriaDTO.class);
        responseDTO.setIdUsuarioRegistro(guardada.getUsuarioRegistro().getIdUsuario());
        if (guardada.getUsuarioEditar() != null) {
            responseDTO.setIdUsuarioEditar(guardada.getUsuarioEditar().getIdUsuario());
        }
        if (guardada.getUsuarioEliminar() != null) {
            responseDTO.setIdUsuarioEliminar(guardada.getUsuarioEliminar().getIdUsuario());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Optional<Auditoria> opt = auditoriaService.listId(id);
        if (opt.isPresent()) {
            Auditoria a = opt.get();
            AuditoriaDTO dto = m.map(a, AuditoriaDTO.class);
            if (a.getUsuarioRegistro() != null) {
                dto.setIdUsuarioRegistro(a.getUsuarioRegistro().getIdUsuario());
            }
            if (a.getUsuarioEditar() != null) {
                dto.setIdUsuarioEditar(a.getUsuarioEditar().getIdUsuario());
            }
            if (a.getUsuarioEliminar() != null) {
                dto.setIdUsuarioEliminar(a.getUsuarioEliminar().getIdUsuario());
            }
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro de auditoría no encontrado");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Auditoria> auditoria = auditoriaService.listId(id);
        if (auditoria.isPresent()) {
            auditoriaService.delete(id);
            return ResponseEntity.ok("Auditoría inactivada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro de auditoría no encontrado");
        }
    }
}
