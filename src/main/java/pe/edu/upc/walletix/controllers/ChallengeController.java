package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.ChallengeDTO;
import pe.edu.upc.walletix.entities.Challenges;
import pe.edu.upc.walletix.servicesinterfaces.IChallengeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/retos")
public class ChallengeController {
    @Autowired
    private IChallengeService cS;

    @GetMapping
    public ResponseEntity<List<ChallengeDTO>> listar(){
        ModelMapper m = new ModelMapper();
        List<ChallengeDTO> listaRetos = cS.list().stream()
                .map(y -> m.map(y, ChallengeDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaRetos);
    }
    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody ChallengeDTO dto){
        if (dto.getEndDateChallenge().isBefore(dto.getStartDateChallenge())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        ModelMapper m = new ModelMapper();
        Challenges c = m.map(dto, Challenges.class);
        Challenges cur = cS.insert(c);
        ChallengeDTO responseDTO = m.map(cur, ChallengeDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Challenges> reto = cS.listId(id);
        if (reto.isPresent()) {
            ChallengeDTO dto = m.map(reto.get(), ChallengeDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto no encontrado");
        }
    }
    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody ChallengeDTO dto) {
        if (dto.getEndDateChallenge().isBefore(dto.getStartDateChallenge())) {
            return ResponseEntity.badRequest()
                    .body("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        Optional<Challenges> existente = cS.listId(dto.getIdChallenge());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto no encontrado");
        }
        Challenges ch = existente.get();
        ch.setTitleChallenge(dto.getTitleChallenge());
        ch.setDescriptionChallenge(dto.getDescriptionChallenge());
        ch.setTargetAmountChallenge(dto.getTargetAmountChallenge());
        ch.setRewardPointsChallenge(dto.getRewardPointsChallenge());
        ch.setStartDateChallenge(dto.getStartDateChallenge());
        ch.setEndDateChallenge(dto.getEndDateChallenge());
        ch.setMinInitialBalanceChallenge(dto.getMinInitialBalanceChallenge());
        ch.setMinAgeRequirementChallenge(dto.getMinAgeRequirementChallenge());
        ch.setMaxDebtAllowedChallenge(dto.getMaxDebtAllowedChallenge());
        ch.setCustomChallenge(dto.isCustomChallenge());
        cS.update(ch);
        return ResponseEntity.ok("Reto actualizado correctamente");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Challenges> reto = cS.listId(id);
        if (reto.isPresent()) {
            try {
                cS.delete(id);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar el reto porque tiene usuarios asociados");
            }
            return ResponseEntity.ok("Reto eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Reto no encontrado");
        }
    }
}