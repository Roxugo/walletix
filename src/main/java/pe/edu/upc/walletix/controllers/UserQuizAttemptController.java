package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.LearningProgressDTO;
import pe.edu.upc.walletix.dtos.UserQuizAttemptDTO;
import pe.edu.upc.walletix.entities.Microlessons;
import pe.edu.upc.walletix.entities.UserQuizAttempts;
import pe.edu.upc.walletix.entities.Users;
import pe.edu.upc.walletix.servicesinterfaces.IMicrolessonService;
import pe.edu.upc.walletix.servicesinterfaces.IUserQuizAttemptService;
import pe.edu.upc.walletix.servicesinterfaces.IUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/intentos-quiz")
public class UserQuizAttemptController {
    @Autowired
    private IUserQuizAttemptService aS;
    @Autowired
    private IUserService uS;
    @Autowired
    private IMicrolessonService mS;

    @GetMapping
    public ResponseEntity<List<UserQuizAttemptDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<UserQuizAttemptDTO> listaIntentos = aS.list().stream()
                .map(y -> m.map(y, UserQuizAttemptDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaIntentos);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UserQuizAttemptDTO dto) {
        if (dto.getScoreUserQuizAttempt() < 0 || dto.getScoreUserQuizAttempt() > 100) {
            return ResponseEntity.badRequest()
                    .body("La nota debe estar entre 0 y 100");
        }
        Optional<Users> usuario = uS.listId(dto.getIdUser());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
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
        UserQuizAttempts c = m.map(dto, UserQuizAttempts.class);
        c.setUser(usuario.get());
        c.setMicrolesson(microleccion.get());
        // Aprueba si su nota llega a la nota mínima de la microlección
        c.setPassedUserQuizAttempt(dto.getScoreUserQuizAttempt() >= microleccion.get().getPassingScoreMicrolesson());
        UserQuizAttempts cur = aS.insert(c);
        UserQuizAttemptDTO responseDTO = m.map(cur, UserQuizAttemptDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<UserQuizAttempts> intento = aS.listId(id);
        if (intento.isPresent()) {
            UserQuizAttemptDTO dto = m.map(intento.get(), UserQuizAttemptDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UserQuizAttemptDTO dto) {
        if (dto.getScoreUserQuizAttempt() < 0 || dto.getScoreUserQuizAttempt() > 100) {
            return ResponseEntity.badRequest()
                    .body("La nota debe estar entre 0 y 100");
        }
        Optional<UserQuizAttempts> existente = aS.listId(dto.getIdUserQuizAttempt());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
        UserQuizAttempts a = existente.get();
        a.setScoreUserQuizAttempt(dto.getScoreUserQuizAttempt());
        a.setPassedUserQuizAttempt(dto.getScoreUserQuizAttempt() >= a.getMicrolesson().getPassingScoreMicrolesson());
        aS.update(a);
        return ResponseEntity.ok("Intento actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UserQuizAttempts> intento = aS.listId(id);
        if (intento.isPresent()) {
            aS.delete(id);
            return ResponseEntity.ok("Intento eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
    }

    // Query nativo: progreso de aprendizaje del usuario. Ej: /intentos-quiz/progreso/1
    @GetMapping("/progreso/{idUser}")
    public ResponseEntity<?> progreso(@PathVariable int idUser) {
        if (uS.listId(idUser).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        List<Object[]> lista = aS.progresoPorUsuario(idUser);
        List<LearningProgressDTO> respuesta = new ArrayList<>();
        for (Object[] fila : lista) {
            LearningProgressDTO dto = new LearningProgressDTO();
            dto.setIdMicrolesson(((Number) fila[0]).intValue());
            dto.setTitleMicrolesson((String) fila[1]);
            dto.setAttempts(((Number) fila[2]).intValue());
            dto.setBestScore(((Number) fila[3]).intValue());
            dto.setPassed((Boolean) fila[4]);
            respuesta.add(dto);
        }
        return ResponseEntity.ok(respuesta);
    }
}
