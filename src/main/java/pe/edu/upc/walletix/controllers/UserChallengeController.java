package pe.edu.upc.walletix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.UserChallengeDTO;
import pe.edu.upc.walletix.entities.Challenges;
import pe.edu.upc.walletix.entities.UserChallenges;
import pe.edu.upc.walletix.entities.Users;
import pe.edu.upc.walletix.servicesinterfaces.IChallengeService;
import pe.edu.upc.walletix.servicesinterfaces.IUserChallengeService;
import pe.edu.upc.walletix.servicesinterfaces.IUserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/retos-usuarios")
public class UserChallengeController {
    @Autowired
    private IUserChallengeService ucS;
    @Autowired
    private IUserService uS;
    @Autowired
    private IChallengeService cS;

    private UserChallengeDTO convertirADTO(UserChallenges uc) {
        UserChallengeDTO dto = new UserChallengeDTO();
        dto.setIdUserChallenge(uc.getIdUserChallenge());
        dto.setIdUser(uc.getUser().getIdUser());
        dto.setIdChallenge(uc.getChallenge().getIdChallenge());
        dto.setInitialBalanceUserChallenge(uc.getInitialBalanceUserChallenge());
        dto.setCurrentProgressAmountUserChallenge(uc.getCurrentProgressAmountUserChallenge());
        dto.setProgressPercentageUserChallenge(uc.getProgressPercentageUserChallenge());
        dto.setStatusUserChallenge(uc.getStatusUserChallenge());
        return dto;
    }

    private String validar(UserChallengeDTO dto) {
        BigDecimal p = dto.getProgressPercentageUserChallenge();
        if (p != null && (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(new BigDecimal("100")) > 0)) {
            return "El porcentaje de progreso debe estar entre 0 y 100";
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<UserChallengeDTO>> listar(){
        List<UserChallengeDTO> lista = ucS.list().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody UserChallengeDTO dto){
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<Users> usuario = uS.listId(dto.getIdUser());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Optional<Challenges> reto = cS.listId(dto.getIdChallenge());
        if (reto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto no encontrado");
        }

        UserChallenges uc = new UserChallenges();
        uc.setUser(usuario.get());
        uc.setChallenge(reto.get());
        uc.setInitialBalanceUserChallenge(dto.getInitialBalanceUserChallenge());
        uc.setCurrentProgressAmountUserChallenge(dto.getCurrentProgressAmountUserChallenge());
        uc.setProgressPercentageUserChallenge(dto.getProgressPercentageUserChallenge());
        uc.setStatusUserChallenge(dto.getStatusUserChallenge());
        UserChallenges cur = ucS.insert(uc);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(cur));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<UserChallenges> uc = ucS.listId(id);
        if (uc.isPresent()) {
            return ResponseEntity.ok(convertirADTO(uc.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto de usuario no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody UserChallengeDTO dto) {
        String error = validar(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Optional<UserChallenges> existente = ucS.listId(dto.getIdUserChallenge());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto de usuario no encontrado");
        }
        Optional<Users> usuario = uS.listId(dto.getIdUser());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Optional<Challenges> reto = cS.listId(dto.getIdChallenge());
        if (reto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto no encontrado");
        }
        UserChallenges uc = existente.get();
        uc.setUser(usuario.get());
        uc.setChallenge(reto.get());
        uc.setInitialBalanceUserChallenge(dto.getInitialBalanceUserChallenge());
        uc.setCurrentProgressAmountUserChallenge(dto.getCurrentProgressAmountUserChallenge());
        uc.setProgressPercentageUserChallenge(dto.getProgressPercentageUserChallenge());
        uc.setStatusUserChallenge(dto.getStatusUserChallenge());
        ucS.update(uc);
        return ResponseEntity.ok("Reto de usuario actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UserChallenges> uc = ucS.listId(id);
        if (uc.isPresent()) {
            ucS.delete(id);
            return ResponseEntity.ok("Reto de usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto de usuario no encontrado");
        }
    }
}