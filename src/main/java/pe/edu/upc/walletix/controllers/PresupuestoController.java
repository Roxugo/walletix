package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.PresupuestoDto;
import pe.edu.upc.walletix.entities.Presupuesto;
import pe.edu.upc.walletix.servicesinterfaces.PresupuestoServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {
    @Autowired
    private PresupuestoServiceInterface presupuestoService;

    @GetMapping
    public ResponseEntity<List<PresupuestoDto>> listar() {
        ModelMapper mapper = new ModelMapper();
        List<PresupuestoDto> lista = presupuestoService.listar().stream()
                .map(presupuesto -> toDto(presupuesto, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody PresupuestoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Presupuesto presupuesto = toEntity(dto);
        Presupuesto registrado = presupuestoService.registrar(presupuesto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Presupuesto> presupuesto = presupuestoService.buscarPorId(id);
        if (presupuesto.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        return ResponseEntity.ok(toDto(presupuesto.get(), new ModelMapper()));
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody PresupuestoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Presupuesto> existente = presupuestoService.buscarPorId(dto.getIdPresupuesto());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");

        Presupuesto presupuesto = existente.get();
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setMontoAsignado(dto.getMontoAsignado());
        presupuesto.setEstado(dto.getEstado());
        presupuesto.setUsuario(dto.getIdUsuario());
        presupuesto.setCategoria(dto.getIdCategoria());
        presupuestoService.actualizar(presupuesto);
        return ResponseEntity.ok("Presupuesto actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (presupuestoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Presupuesto no encontrado");
        }
        presupuestoService.eliminar(id);
        return ResponseEntity.ok("Presupuesto eliminado correctamente");
    }

    private Presupuesto toEntity(PresupuestoDto dto) {
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setIdPresupuesto(dto.getIdPresupuesto());
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setMontoAsignado(dto.getMontoAsignado());
        presupuesto.setEstado(dto.getEstado());
        presupuesto.setUsuario(dto.getIdUsuario());
        presupuesto.setCategoria(dto.getIdCategoria());
        return presupuesto;
    }

    private PresupuestoDto toDto(Presupuesto presupuesto, ModelMapper mapper) {
        PresupuestoDto dto = mapper.map(presupuesto, PresupuestoDto.class);
        dto.setIdUsuario(presupuesto.getUsuario());
        dto.setIdCategoria(presupuesto.getCategoria());
        return dto;
    }

    private String validar(PresupuestoDto dto) {
        if (dto == null) return "Se requiere información del presupuesto";
        if (dto.getIdPresupuesto() < 0) return "El id del presupuesto no puede ser negativo";
        if (dto.getMes() < 1 || dto.getMes() > 12) return "El mes debe estar entre 1 y 12";
        if (dto.getAnio() <= 0) return "El año debe ser positivo";
        if (dto.getMontoAsignado() <= 0) return "El monto asignado debe ser mayor que cero";
        if (dto.getIdUsuario() == null) return "Se requiere un usuario";
        if (dto.getIdCategoria() == null) return "Se requiere una categoría";
        return null;
    }
}
