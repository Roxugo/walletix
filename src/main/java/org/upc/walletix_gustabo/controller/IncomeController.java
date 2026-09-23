package org.upc.walletix_gustabo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upc.walletix_gustabo.entity.Incomes;
import org.upc.walletix_gustabo.servicesinterfaces.IIncomesService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    @Autowired
    private IIncomesService iS;

    @GetMapping
    public ResponseEntity<List<Incomes>> listar() {
        return ResponseEntity.ok(iS.list());
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody Incomes income) {
        if (!esValido(income)) {
            return ResponseEntity.badRequest().body(
                    "El ingreso requiere un monto mayor que cero, fecha, tipo, frecuencia y fuente"
            );
        }

        Incomes registrado = iS.insert(income);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Incomes> income = iS.listId(id);
        if (income.isPresent()) {
            return ResponseEntity.ok(income.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
    }

    @PutMapping("/actualiza")
    public ResponseEntity<?> actualizar(@RequestBody Incomes income) {
        if (income == null || income.getId_income() <= 0) {
            return ResponseEntity.badRequest().body("Se requiere un id de ingreso válido");
        }
        if (!esValido(income)) {
            return ResponseEntity.badRequest().body(
                    "El ingreso requiere un monto mayor que cero, fecha, tipo, frecuencia y fuente"
            );
        }

        Optional<Incomes> existente = iS.listId(income.getId_income());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        }

        iS.update(income);
        return ResponseEntity.ok("Ingreso actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Incomes> income = iS.listId(id);
        if (income.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        }

        iS.delete(id);
        return ResponseEntity.ok("Ingreso eliminado correctamente");
    }

    private boolean esValido(Incomes income) {
        return income != null
                && income.getCantidad() > 0
                && income.getFecha() != null
                && income.getTipo_ingreso() != null
                && !income.getTipo_ingreso().isBlank()
                && income.getFrequency() != null
                && income.getSource() != null
                && !income.getSource().isBlank();
    }
}
