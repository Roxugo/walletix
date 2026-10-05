package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.PreguntaQuizDTO;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.entities.PreguntaQuiz;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;
import pe.edu.upc.walletix.servicesinterfaces.IPreguntaQuizService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Preguntas de quiz", description = "Preguntas del quiz de cada microlección")
@RestController
@RequestMapping("/preguntas-quiz")
public class PreguntaQuizController {
    @Autowired
    private IPreguntaQuizService preguntaQuizService;
    @Autowired
    private IMicroleccionService microleccionService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(PreguntaQuizDTO preguntaQuizDTO) {
        if (estaVacio(preguntaQuizDTO.getEnunciadoPreguntaQuiz())) {
            return "El enunciado es obligatorio";
        }
        if (estaVacio(preguntaQuizDTO.getOpcionAPreguntaQuiz()) || estaVacio(preguntaQuizDTO.getOpcionBPreguntaQuiz())) {
            return "Las opciones A y B son obligatorias";
        }
        String opcionCorrecta = preguntaQuizDTO.getOpcionCorrectaPreguntaQuiz();
        if (opcionCorrecta == null || !opcionCorrecta.toUpperCase().matches("[ABCD]")) {
            return "La opción correcta debe ser A, B, C o D";
        }
        if (opcionCorrecta.equalsIgnoreCase("C") && estaVacio(preguntaQuizDTO.getOpcionCPreguntaQuiz())) {
            return "La opción correcta es C, pero la opción C está vacía";
        }
        if (opcionCorrecta.equalsIgnoreCase("D") && estaVacio(preguntaQuizDTO.getOpcionDPreguntaQuiz())) {
            return "La opción correcta es D, pero la opción D está vacía";
        }
        if (estaVacio(preguntaQuizDTO.getExplicacionPreguntaQuiz())) {
            return "La explicación es obligatoria";
        }
        return null;
    }

    @Operation(summary = "Listar las preguntas de quiz activas")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<PreguntaQuizDTO>> listar() {
        ModelMapper modelMapper = new ModelMapper();
        List<PreguntaQuizDTO> listaPreguntas = preguntaQuizService.list().stream()
                .map(preguntaQuiz -> modelMapper.map(preguntaQuiz, PreguntaQuizDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaPreguntas);
    }

    @Operation(summary = "Registrar una pregunta de quiz (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody PreguntaQuizDTO preguntaQuizDTO) {
        String error = validar(preguntaQuizDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Microleccion> microleccion = microleccionService.listId(preguntaQuizDTO.getIdMicroleccion());
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        if (!microleccion.get().getTipoMicroleccion().equals("leccion")) {
            return ResponseEntity.badRequest()
                    .body("Solo las microlecciones de tipo leccion tienen quiz");
        }

        ModelMapper modelMapper = new ModelMapper();
        PreguntaQuiz nuevaPregunta = modelMapper.map(preguntaQuizDTO, PreguntaQuiz.class);
        nuevaPregunta.setMicroleccion(microleccion.get());
        nuevaPregunta.setOpcionCorrectaPreguntaQuiz(preguntaQuizDTO.getOpcionCorrectaPreguntaQuiz().toUpperCase());
        PreguntaQuiz preguntaRegistrada = preguntaQuizService.insert(nuevaPregunta);
        PreguntaQuizDTO respuestaDTO = modelMapper.map(preguntaRegistrada, PreguntaQuizDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
    }

    @Operation(summary = "Buscar una pregunta de quiz por su id")
    @GetMapping("/{idPreguntaQuiz}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idPreguntaQuiz) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<PreguntaQuiz> preguntaQuiz = preguntaQuizService.listId(idPreguntaQuiz);
        if (preguntaQuiz.isPresent()) {
            PreguntaQuizDTO preguntaQuizDTO = modelMapper.map(preguntaQuiz.get(), PreguntaQuizDTO.class);
            return ResponseEntity.ok(preguntaQuizDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
    }

    @Operation(summary = "Actualizar una pregunta de quiz (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody PreguntaQuizDTO preguntaQuizDTO) {
        String error = validar(preguntaQuizDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<PreguntaQuiz> preguntaExistente = preguntaQuizService.listId(preguntaQuizDTO.getIdPreguntaQuiz());
        if (preguntaExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
        Optional<Microleccion> microleccion = microleccionService.listId(preguntaQuizDTO.getIdMicroleccion());
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        PreguntaQuiz preguntaQuiz = preguntaExistente.get();
        preguntaQuiz.setMicroleccion(microleccion.get());
        preguntaQuiz.setEnunciadoPreguntaQuiz(preguntaQuizDTO.getEnunciadoPreguntaQuiz());
        preguntaQuiz.setOpcionAPreguntaQuiz(preguntaQuizDTO.getOpcionAPreguntaQuiz());
        preguntaQuiz.setOpcionBPreguntaQuiz(preguntaQuizDTO.getOpcionBPreguntaQuiz());
        preguntaQuiz.setOpcionCPreguntaQuiz(preguntaQuizDTO.getOpcionCPreguntaQuiz());
        preguntaQuiz.setOpcionDPreguntaQuiz(preguntaQuizDTO.getOpcionDPreguntaQuiz());
        preguntaQuiz.setOpcionCorrectaPreguntaQuiz(preguntaQuizDTO.getOpcionCorrectaPreguntaQuiz().toUpperCase());
        preguntaQuiz.setExplicacionPreguntaQuiz(preguntaQuizDTO.getExplicacionPreguntaQuiz());
        preguntaQuizService.update(preguntaQuiz);
        return ResponseEntity.ok("Pregunta actualizada correctamente");
    }

    @Operation(summary = "Eliminar una pregunta de quiz (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{idPreguntaQuiz}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int idPreguntaQuiz) {
        Optional<PreguntaQuiz> preguntaQuiz = preguntaQuizService.listId(idPreguntaQuiz);
        if (preguntaQuiz.isPresent()) {
            preguntaQuizService.delete(idPreguntaQuiz);
            return ResponseEntity.ok("Pregunta eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
    }

    // Preguntas del quiz de una microlección. Ej: /preguntas-quiz/microleccion/1
    @Operation(summary = "Listar las preguntas de una microlección")
    @GetMapping("/microleccion/{idMicroleccion}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> listarPorMicroleccion(@PathVariable int idMicroleccion) {
        if (microleccionService.listId(idMicroleccion).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        ModelMapper modelMapper = new ModelMapper();
        List<PreguntaQuizDTO> listaPreguntas = preguntaQuizService.listarPorMicroleccion(idMicroleccion).stream()
                .map(preguntaQuiz -> modelMapper.map(preguntaQuiz, PreguntaQuizDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaPreguntas);
    }
}
