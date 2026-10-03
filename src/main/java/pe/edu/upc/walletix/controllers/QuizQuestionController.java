package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.QuizQuestionDTO;
import pe.edu.upc.walletix.entities.Microlessons;
import pe.edu.upc.walletix.entities.QuizQuestions;
import pe.edu.upc.walletix.servicesinterfaces.IMicrolessonService;
import pe.edu.upc.walletix.servicesinterfaces.IQuizQuestionService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/preguntas-quiz")
public class cQuizQuestionController {
    @Autowired
    private IQuizQuestionService qS;
    @Autowired
    private IMicrolessonService mS;

    private String validar(QuizQuestionDTO dto) {
        if (dto.getQuestionTextQuizQuestion() == null || dto.getQuestionTextQuizQuestion().isBlank()) {
            return "El enunciado es obligatorio";
        }
        if (dto.getOptionAQuizQuestion() == null || dto.getOptionAQuizQuestion().isBlank()
                || dto.getOptionBQuizQuestion() == null || dto.getOptionBQuizQuestion().isBlank()) {
            return "Las opciones A y B son obligatorias";
        }
        String correcta = dto.getCorrectOptionQuizQuestion();
        if (correcta == null || !correcta.toUpperCase().matches("[ABCD]")) {
            return "La opción correcta debe ser A, B, C o D";
        }
        if (correcta.equalsIgnoreCase("C") && (dto.getOptionCQuizQuestion() == null || dto.getOptionCQuizQuestion().isBlank())) {
            return "La opción correcta es C, pero la opción C está vacía";
        }
        if (correcta.equalsIgnoreCase("D") && (dto.getOptionDQuizQuestion() == null || dto.getOptionDQuizQuestion().isBlank())) {
            return "La opción correcta es D, pero la opción D está vacía";
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<QuizQuestionDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<QuizQuestionDTO> listaPreguntas = qS.list().stream()
                .map(y -> m.map(y, QuizQuestionDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaPreguntas);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody QuizQuestionDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Microlessons> microleccion = mS.listId(dto.getIdMicrolesson());
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        if (!microleccion.get().getTypeMicrolesson().equals("leccion")) {
            return ResponseEntity.badRequest()
                    .body("Solo las microlecciones de tipo leccion tienen quiz");
        }

        ModelMapper m = new ModelMapper();
        QuizQuestions c = m.map(dto, QuizQuestions.class);
        c.setMicrolesson(microleccion.get());
        c.setCorrectOptionQuizQuestion(dto.getCorrectOptionQuizQuestion().toUpperCase());
        QuizQuestions cur = qS.insert(c);
        QuizQuestionDTO responseDTO = m.map(cur, QuizQuestionDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<QuizQuestions> pregunta = qS.listId(id);
        if (pregunta.isPresent()) {
            QuizQuestionDTO dto = m.map(pregunta.get(), QuizQuestionDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody QuizQuestionDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<QuizQuestions> existente = qS.listId(dto.getIdQuizQuestion());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
        Optional<Microlessons> microleccion = mS.listId(dto.getIdMicrolesson());
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        QuizQuestions q = existente.get();
        q.setMicrolesson(microleccion.get());
        q.setQuestionTextQuizQuestion(dto.getQuestionTextQuizQuestion());
        q.setOptionAQuizQuestion(dto.getOptionAQuizQuestion());
        q.setOptionBQuizQuestion(dto.getOptionBQuizQuestion());
        q.setOptionCQuizQuestion(dto.getOptionCQuizQuestion());
        q.setOptionDQuizQuestion(dto.getOptionDQuizQuestion());
        q.setCorrectOptionQuizQuestion(dto.getCorrectOptionQuizQuestion().toUpperCase());
        q.setExplanationQuizQuestion(dto.getExplanationQuizQuestion());
        qS.update(q);
        return ResponseEntity.ok("Pregunta actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<QuizQuestions> pregunta = qS.listId(id);
        if (pregunta.isPresent()) {
            qS.delete(id);
            return ResponseEntity.ok("Pregunta eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pregunta no encontrada");
        }
    }

    // Preguntas del quiz de una microlección. Ej: /preguntas-quiz/microleccion/1
    @GetMapping("/microleccion/{idMicrolesson}")
    public ResponseEntity<?> listarPorMicroleccion(@PathVariable int idMicrolesson) {
        if (mS.listId(idMicrolesson).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        ModelMapper m = new ModelMapper();
        List<QuizQuestionDTO> listaPreguntas = qS.listByMicrolesson(idMicrolesson).stream()
                .map(y -> m.map(y, QuizQuestionDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaPreguntas);
    }
}
