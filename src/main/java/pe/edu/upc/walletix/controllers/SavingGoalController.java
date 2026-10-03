package pe.edu.upc.walletix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.servicesinterfaces.ISavingGoalService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/saving-goals")
public class SavingGoalController {
    @Autowired
    private ISavingGoalService sgS;

    @GetMapping
    public ResponseEntity<List<SavingGoal>> listar() {
        return ResponseEntity.ok(sgS.list());
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody SavingGoal savingGoal) {
        if (!esValido(savingGoal)) {
            return ResponseEntity.badRequest().body(
                    "La meta requiere título, montos válidos, fecha límite y estado"
            );
        }

        SavingGoal registrada = sgS.insert(savingGoal);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<SavingGoal> savingGoal = sgS.listId(id);
        if (savingGoal.isPresent()) {
            return ResponseEntity.ok(savingGoal.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
    }

    @PutMapping("/actualiza")
    public ResponseEntity<?> actualizar(@RequestBody SavingGoal savingGoal) {
        if (savingGoal == null || savingGoal.getIdSavingGoal() <= 0) {
            return ResponseEntity.badRequest().body("Se requiere un id de meta válido");
        }
        if (!esValido(savingGoal)) {
            return ResponseEntity.badRequest().body(
                    "La meta requiere título, montos válidos, fecha límite y estado"
            );
        }

        Optional<SavingGoal> existente = sgS.listId(savingGoal.getIdSavingGoal());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        }

        sgS.update(savingGoal);
        return ResponseEntity.ok("Meta de ahorro actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<SavingGoal> savingGoal = sgS.listId(id);
        if (savingGoal.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        }

        sgS.delete(id);
        return ResponseEntity.ok("Meta de ahorro eliminada correctamente");
    }

    private boolean esValido(SavingGoal savingGoal) {
        return savingGoal != null
                && savingGoal.getTitle() != null
                && !savingGoal.getTitle().isBlank()
                && savingGoal.getTargetAmount() != null
                && savingGoal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                && savingGoal.getCurrentAmount() != null
                && savingGoal.getCurrentAmount().compareTo(BigDecimal.ZERO) >= 0
                && savingGoal.getDeadline() != null
                && savingGoal.getStatus() != null;
    }
}
