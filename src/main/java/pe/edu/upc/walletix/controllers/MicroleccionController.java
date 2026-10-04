package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MicroleccionDTO;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;
import pe.edu.upc.walletix.servicesinterfaces.IPreguntaQuizService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/microlecciones")
public class MicroleccionController {
    @Autowired
    private IMicroleccionService microleccionService;
    @Autowired
    private IPreguntaQuizService preguntaQuizService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(MicroleccionDTO microleccionDTO) {
        if (estaVacio(microleccionDTO.getTituloMicroleccion())) {
            return "El título es obligatorio";
        }
        if (estaVacio(microleccionDTO.getDescripcionMicroleccion())) {
            return "La descripción es obligatoria";
        }
        if (estaVacio(microleccionDTO.getContenidoMicroleccion())) {
            return "El contenido es obligatorio";
        }
        if (estaVacio(microleccionDTO.getUrlMediaMicroleccion())) {
            return "La URL de la imagen o video es obligatoria";
        }
        if (estaVacio(microleccionDTO.getCategoriaEducativaMicroleccion())) {
            return "La categoría educativa es obligatoria";
        }
        if (microleccionDTO.getTipoMicroleccion() == null
                || !(microleccionDTO.getTipoMicroleccion().equalsIgnoreCase("tip") || microleccionDTO.getTipoMicroleccion().equalsIgnoreCase("leccion"))) {
            return "El tipo debe ser tip o leccion";
        }
        if (estaVacio(microleccionDTO.getTituloQuizMicroleccion())) {
            return "El título del quiz es obligatorio";
        }
        if (microleccionDTO.getPuntajeAprobatorioMicroleccion() < 0 || microleccionDTO.getPuntajeAprobatorioMicroleccion() > 100) {
            return "El puntaje aprobatorio debe estar entre 0 y 100";
        }
        if (microleccionDTO.getPuntosRecompensaMicroleccion() < 0) {
            return "Los puntos de recompensa no pueden ser negativos";
        }
        return null;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<MicroleccionDTO>> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<MicroleccionDTO> listaMicrolecciones = microleccionService.list().stream()
                .map(microleccion -> modelMapper.map(microleccion, MicroleccionDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaMicrolecciones);
    }

    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody MicroleccionDTO microleccionDTO) {
        String error = validar(microleccionDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ModelMapper modelMapper = new ModelMapper();
        microleccionDTO.setTipoMicroleccion(microleccionDTO.getTipoMicroleccion().toLowerCase());
        Microleccion nuevaMicroleccion = modelMapper.map(microleccionDTO, Microleccion.class);
        Microleccion microleccionRegistrada = microleccionService.insert(nuevaMicroleccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(microleccionRegistrada, MicroleccionDTO.class));
    }

    @GetMapping("/{idMicroleccion}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idMicroleccion) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<Microleccion> microleccion = microleccionService.listId(idMicroleccion);
        if (microleccion.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(microleccion.get(), MicroleccionDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody MicroleccionDTO microleccionDTO) {
        String error = validar(microleccionDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Microleccion> microleccionExistente = microleccionService.listId(microleccionDTO.getIdMicroleccion());
        if (microleccionExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        Microleccion microleccion = microleccionExistente.get();
        microleccion.setTituloMicroleccion(microleccionDTO.getTituloMicroleccion());
        microleccion.setDescripcionMicroleccion(microleccionDTO.getDescripcionMicroleccion());
        microleccion.setContenidoMicroleccion(microleccionDTO.getContenidoMicroleccion());
        microleccion.setUrlMediaMicroleccion(microleccionDTO.getUrlMediaMicroleccion());
        microleccion.setCategoriaEducativaMicroleccion(microleccionDTO.getCategoriaEducativaMicroleccion());
        microleccion.setTipoMicroleccion(microleccionDTO.getTipoMicroleccion().toLowerCase());
        microleccion.setBloqueadoMicroleccion(microleccionDTO.isBloqueadoMicroleccion());
        microleccion.setTituloQuizMicroleccion(microleccionDTO.getTituloQuizMicroleccion());
        microleccion.setPuntajeAprobatorioMicroleccion(microleccionDTO.getPuntajeAprobatorioMicroleccion());
        microleccion.setPuntosRecompensaMicroleccion(microleccionDTO.getPuntosRecompensaMicroleccion());
        microleccionService.update(microleccion);
        return ResponseEntity.ok("Microlección actualizada correctamente");
    }

    @DeleteMapping("/{idMicroleccion}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int idMicroleccion) {
        Optional<Microleccion> microleccion = microleccionService.listId(idMicroleccion);
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        if (!preguntaQuizService.listarPorMicroleccion(idMicroleccion).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: la microlección tiene preguntas activas");
        }
        microleccionService.delete(idMicroleccion);
        return ResponseEntity.ok("Microlección eliminada correctamente");
    }

    // JPQL: consejos y microlecciones por categoría (US31). Ej: /microlecciones/categoria?categoria=Ahorro
    @GetMapping("/categoria")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorCategoria(@RequestParam("categoria") String categoria) {
        ModelMapper modelMapper = new ModelMapper();
        List<MicroleccionDTO> listaMicrolecciones = microleccionService.buscarPorCategoria(categoria).stream()
                .map(microleccion -> modelMapper.map(microleccion, MicroleccionDTO.class))
                .collect(Collectors.toList());
        if (listaMicrolecciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No hay microlecciones en la categoría " + categoria);
        }
        return ResponseEntity.ok(listaMicrolecciones);
    }
}
