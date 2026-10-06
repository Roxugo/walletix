package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.AuditoriaDTO;
import pe.edu.upc.walletix.entities.Auditoria;
import pe.edu.upc.walletix.servicesinterfaces.IAuditoriaService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Solo lectura: las auditorías se crean y actualizan solas al registrar, editar o eliminar un usuario.
// No se pueden crear ni borrar a mano, para que el historial sea confiable
@Tag(name = "Auditoría", description = "Historial automático de quién registró, editó o eliminó cada usuario (solo lectura, solo ADMIN)")
@RestController
@RequestMapping("/auditorias")
public class AuditoriaController {

    @Autowired
    private IAuditoriaService auditoriaService;

    @Operation(summary = "Listar todo el historial de auditoría, incluidos los registros dados de baja")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @Operation(summary = "Buscar una auditoría por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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
}
