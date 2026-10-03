package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.IngresoDto;
import pe.edu.upc.walletix.entities.Ingreso;
import pe.edu.upc.walletix.servicesinterfaces.IngresoServiceInterface;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ingresos")
public class IngresoController {
    @Autowired
    private IngresoServiceInterface ingresoService;

    @GetMapping
    public ResponseEntity<List<IngresoDto>> listar() {
        ModelMapper mapper = new ModelMapper();
        List<IngresoDto> lista = ingresoService.listar().stream()
                .map(ingreso -> toDto(ingreso, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody IngresoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Ingreso registrado = ingresoService.registrar(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrado, new ModelMapper()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Ingreso> ingreso = ingresoService.buscarPorId(id);
        if (ingreso.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        return ResponseEntity.ok(toDto(ingreso.get(), new ModelMapper()));
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody IngresoDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<Ingreso> existente = ingresoService.buscarPorId(dto.getIdIngreso());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");

        Ingreso ingreso = existente.get();
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setTipoIngreso(dto.getTipoIngreso());
        ingreso.setFrecuencia(dto.getFrecuencia());
        ingreso.setFuente(dto.getFuente());
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setEstado(dto.getEstado());
        ingreso.setUsuario(dto.getIdUsuario());
        ingreso.setCategoria(dto.getIdCategoria());
        ingresoService.actualizar(ingreso);
        return ResponseEntity.ok("Ingreso actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (ingresoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ingreso no encontrado");
        }
        ingresoService.eliminar(id);
        return ResponseEntity.ok("Ingreso eliminado correctamente");
    }

    private Ingreso toEntity(IngresoDto dto) {
        Ingreso ingreso = new Ingreso();
        ingreso.setIdIngreso(dto.getIdIngreso());
        ingreso.setMonto(dto.getMonto());
        ingreso.setFecha(dto.getFecha());
        ingreso.setTipoIngreso(dto.getTipoIngreso());
        ingreso.setFrecuencia(dto.getFrecuencia());
        ingreso.setFuente(dto.getFuente());
        ingreso.setDescripcion(dto.getDescripcion());
        ingreso.setEstado(dto.getEstado());
        ingreso.setUsuario(dto.getIdUsuario());
        ingreso.setCategoria(dto.getIdCategoria());
        return ingreso;
    }

    private IngresoDto toDto(Ingreso ingreso, ModelMapper mapper) {
        IngresoDto dto = mapper.map(ingreso, IngresoDto.class);
        dto.setIdUsuario(ingreso.getUsuario());
        dto.setIdCategoria(ingreso.getCategoria());
        return dto;
    }

    private String validar(IngresoDto dto) {
        if (dto == null) return "Se requiere información del ingreso";
        if (dto.getIdIngreso() < 0) return "El id del ingreso no puede ser negativo";
        if (dto.getMonto() <= 0) return "El monto debe ser mayor que cero";
        if (dto.getFecha() == null) return "Se requiere una fecha";
        if (dto.getTipoIngreso() == null || dto.getTipoIngreso().isBlank()) return "Se requiere el tipo de ingreso";
        if (dto.getFrecuencia() == null || dto.getFrecuencia().isBlank()) return "Se requiere la frecuencia";
        if (dto.getFuente() == null || dto.getFuente().isBlank()) return "Se requiere la fuente";
        if (dto.getIdUsuario() == null) return "Se requiere un usuario";
        if (dto.getIdCategoria() == null) return "Se requiere una categoría";
        return null;
    }
}
