package org.upc.walletix_gustabo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upc.walletix_gustabo.entity.Saving_goals;
import org.upc.walletix_gustabo.servicesinterfaces.ISaving_goalsService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/saving-goals")
public class SavingGoalController {

    @Autowired
    private ISaving_goalsService sgS;

    @GetMapping
    public ResponseEntity<List<Saving_goals>> listar() {
        return ResponseEntity.ok(sgS.list());
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody Saving_goals savingGoal) {
        if (!esValido(savingGoal)) {
            return ResponseEntity.badRequest().body(
                    "La meta requiere título, montos válidos, fecha límite y estado"
            );
        }

        Saving_goals registrada = sgS.insert(savingGoal);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Saving_goals> savingGoal = sgS.listId(id);
        if (savingGoal.isPresent()) {
            return ResponseEntity.ok(savingGoal.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
    }

    @PutMapping("/actualiza")
    public ResponseEntity<?> actualizar(@RequestBody Saving_goals savingGoal) {
        if (savingGoal == null || savingGoal.getId_saving_goals() <= 0) {
            return ResponseEntity.badRequest().body("Se requiere un id de meta válido");
        }
        if (!esValido(savingGoal)) {
            return ResponseEntity.badRequest().body(
                    "La meta requiere título, montos válidos, fecha límite y estado"
            );
        }

        Optional<Saving_goals> existente = sgS.listId(savingGoal.getId_saving_goals());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        }

        sgS.update(savingGoal);
        return ResponseEntity.ok("Meta de ahorro actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Saving_goals> savingGoal = sgS.listId(id);
        if (savingGoal.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        }

        sgS.delete(id);
        return ResponseEntity.ok("Meta de ahorro eliminada correctamente");
    }

    private boolean esValido(Saving_goals savingGoal) {
        return savingGoal != null
                && savingGoal.getTitulo() != null
                && !savingGoal.getTitulo().isBlank()
                && savingGoal.getTarget_amount() != null
                && savingGoal.getTarget_amount().compareTo(BigDecimal.ZERO) > 0
                && savingGoal.getCurrent_amount() != null
                && savingGoal.getCurrent_amount().compareTo(BigDecimal.ZERO) >= 0
                && savingGoal.getDeadline() != null
                && savingGoal.getStatus() != null;
    }
}
