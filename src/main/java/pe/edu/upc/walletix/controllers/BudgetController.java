package pe.edu.upc.walletix.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.entities.Budgets;
import pe.edu.upc.walletix.servicesinterfaces.IBudgetsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    @Autowired
    private IBudgetsService bS;

    @GetMapping
    public ResponseEntity<List<Budgets>> listar() {
        return ResponseEntity.ok(bS.list());
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody Budgets budget) {
        if (!esValido(budget)) {
            return ResponseEntity.badRequest()
                    .body("Mes debe estar entre 1 y 12, año debe ser positivo e importe asignado debe ser mayor que cero");
        }

        Budgets registrado = bS.insert(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Budgets> budget = bS.listId(id);
        if (budget.isPresent()) {
            return ResponseEntity.ok(budget.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
    }

    @PutMapping("/actualiza")
    public ResponseEntity<?> actualizar(@RequestBody Budgets budget) {
        if (budget.getIdBudget() <= 0) {
            return ResponseEntity.badRequest().body("Se requiere un id de presupuesto válido");
        }
        if (!esValido(budget)) {
            return ResponseEntity.badRequest()
                    .body("Mes debe estar entre 1 y 12, año debe ser positivo e importe asignado debe ser mayor que cero");
        }

        Optional<Budgets> existente = bS.listId(budget.getIdBudget());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        }

        bS.update(budget);
        return ResponseEntity.ok("Presupuesto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Budgets> budget = bS.listId(id);
        if (budget.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        }

        bS.delete(id);
        return ResponseEntity.ok("Presupuesto eliminado correctamente");
    }

    private boolean esValido(Budgets budget) {
        return budget != null
                && budget.getMonth() >= 1
                && budget.getMonth() <= 12
                && budget.getYear() > 0
                && budget.getAllocatedAmount() > 0;
    }
}
