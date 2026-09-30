package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.ExpenseDTO;
import pe.edu.upc.walletix.entities.Expense;
import pe.edu.upc.walletix.servicesinterfaces.IExpenseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gastos")
public class ExpenseController {
    @Autowired
    private IExpenseService eS;

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> listar() {
        ModelMapper m = new ModelMapper();
        List<ExpenseDTO> listaGastos = eS.list().stream()
                .map(y -> m.map(y, ExpenseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaGastos);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody ExpenseDTO dto) {
        if (dto.getDateExpense().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("La fecha del gasto no puede ser futura");
        }

        ModelMapper m = new ModelMapper();
        Expense e = m.map(dto, Expense.class);
        Expense cur = eS.insert(e);
        ExpenseDTO responseDTO = m.map(cur, ExpenseDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper m = new ModelMapper();
        Optional<Expense> mach = eS.listId(id);
        if (mach.isPresent()) {
            ExpenseDTO dto = m.map(mach.get(), ExpenseDTO.class);
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody ExpenseDTO dto) {
        if (dto.getDateExpense().isAfter(java.time.LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("La fecha del gasto no puede ser futura");
        }
        Optional<Expense> existente = eS.listId(dto.getIdExpense());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
        Expense e = existente.get();
        e.setUserIdExpense(dto.getUserIdExpense());
        e.setCategoryIdExpense(dto.getCategoryIdExpense());
        e.setMerchantIdExpense(dto.getMerchantIdExpense());
        e.setAmountExpense(dto.getAmountExpense());
        e.setDateExpense(dto.getDateExpense());
        e.setDescriptionExpense(dto.getDescriptionExpense());
        e.setPaymentMethodExpense(dto.getPaymentMethodExpense());
        e.setMicroexpenseExpense(dto.isMicroexpenseExpense());
        eS.update(e);
        return ResponseEntity.ok("Gasto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Expense> machine = eS.listId(id);
        if (machine.isPresent()) {
            eS.delete(id);
            return ResponseEntity.ok("Gasto eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gasto no encontrado");
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ExpenseDTO>> buscarPorUsuarioYRango(@PathVariable int userId,
                                                                     @RequestParam LocalDate desde,
                                                                     @RequestParam LocalDate hasta) {
        ModelMapper m = new ModelMapper();
        List<ExpenseDTO> lista = eS.buscarPorUsuarioYRango(userId, desde, hasta).stream()
                .map(y -> m.map(y, ExpenseDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
}
