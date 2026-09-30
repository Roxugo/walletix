package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MicrolessonDTO;
import pe.edu.upc.walletix.entities.Microlessons;
import pe.edu.upc.walletix.servicesinterfaces.IMicrolessonService;
import pe.edu.upc.walletix.servicesinterfaces.IQuizQuestionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/microlecciones")
public class MicrolessonController {
    @Autowired
    private IMicrolessonService mS;
    @Autowired
    private IQuizQuestionService qS;

    private String validar(MicrolessonDTO dto) {
        if (dto.getTitleMicrolesson() == null || dto.getTitleMicrolesson().isBlank()) {
            return "El título es obligatorio";
        }
        if (dto.getContentTextMicrolesson() == null || dto.getContentTextMicrolesson().isBlank()) {
            return "El contenido es obligatorio";
        }
        if (dto.getCategoryMicrolesson() == null || dto.getCategoryMicrolesson().isBlank()) {
            return "La categoría es obligatoria";
        }
        if (dto.getTypeMicrolesson() == null
                || !(dto.getTypeMicrolesson().equalsIgnoreCase("tip") || dto.getTypeMicrolesson().equalsIgnoreCase("leccion"))) {
            return "El tipo debe ser tip o leccion";
        }
        if (dto.getPassingScoreMicrolesson() < 0 || dto.getPassingScoreMicrolesson() > 100) {
            return "La nota mínima debe estar entre 0 y 100";
        }
        if (dto.getRewardPointsMicrolesson() < 0) {
            return "Los puntos de recompensa no pueden ser negativos";
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<MicrolessonDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<MicrolessonDTO> lista = mS.list().stream()
                .map(y -> m.map(y, MicrolessonDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody MicrolessonDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ModelMapper m = new ModelMapper();
        dto.setTypeMicrolesson(dto.getTypeMicrolesson().toLowerCase());
        Microlessons c = m.map(dto, Microlessons.class);
        Microlessons cur = mS.insert(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(m.map(cur, MicrolessonDTO.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Microlessons> micro = mS.listId(id);
        if (micro.isPresent()) {
            return ResponseEntity.ok(m.map(micro.get(), MicrolessonDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody MicrolessonDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Microlessons> existente = mS.listId(dto.getIdMicrolesson());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        Microlessons micro = existente.get();
        micro.setTitleMicrolesson(dto.getTitleMicrolesson());
        micro.setDescriptionMicrolesson(dto.getDescriptionMicrolesson());
        micro.setContentTextMicrolesson(dto.getContentTextMicrolesson());
        micro.setMediaUrlMicrolesson(dto.getMediaUrlMicrolesson());
        micro.setCategoryMicrolesson(dto.getCategoryMicrolesson());
        micro.setTypeMicrolesson(dto.getTypeMicrolesson().toLowerCase());
        micro.setLockedMicrolesson(dto.isLockedMicrolesson());
        micro.setQuizTitleMicrolesson(dto.getQuizTitleMicrolesson());
        micro.setPassingScoreMicrolesson(dto.getPassingScoreMicrolesson());
        micro.setRewardPointsMicrolesson(dto.getRewardPointsMicrolesson());
        mS.update(micro);
        return ResponseEntity.ok("Microlección actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Microlessons> micro = mS.listId(id);
        if (micro.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        if (!qS.listByMicrolesson(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: la microlección tiene preguntas registradas");
        }
        mS.delete(id);
        return ResponseEntity.ok("Microlección eliminada correctamente");
    }

    // JPQL: consejos y microlecciones por categoría (US31). Ej: /microlecciones/categoria?categoria=Ahorro
    @GetMapping("/categoria")
    public ResponseEntity<?> buscarPorCategoria(@RequestParam("categoria") String categoria) {
        ModelMapper m = new ModelMapper();
        List<MicrolessonDTO> lista = mS.buscarPorCategoria(categoria).stream()
                .map(y -> m.map(y, MicrolessonDTO.class))
                .collect(Collectors.toList());
        if (lista.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay microlecciones en la categoría " + categoria);
        }
        return ResponseEntity.ok(lista);
    }
}
