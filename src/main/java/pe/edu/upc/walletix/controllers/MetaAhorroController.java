package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MetaAhorroDto;
import pe.edu.upc.walletix.entities.MetaAhorro;
import pe.edu.upc.walletix.servicesinterfaces.MetaAhorroServiceInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metas-ahorro")
public class MetaAhorroController {
    @Autowired
    private MetaAhorroServiceInterface metaAhorroService;

    @GetMapping
    public ResponseEntity<List<MetaAhorroDto>> listar() {
        ModelMapper mapper = new ModelMapper();
        List<MetaAhorroDto> lista = metaAhorroService.listar().stream()
                .map(meta -> toDto(meta, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/web")
    public ResponseEntity<?> registrar(@RequestBody MetaAhorroDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        MetaAhorro registrada = metaAhorroService.registrar(toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrada, new ModelMapper()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<MetaAhorro> meta = metaAhorroService.buscarPorId(id);
        if (meta.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        return ResponseEntity.ok(toDto(meta.get(), new ModelMapper()));
    }

    @PutMapping("/actualiza")
    public ResponseEntity<String> actualizar(@RequestBody MetaAhorroDto dto) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<MetaAhorro> existente = metaAhorroService.buscarPorId(dto.getIdMetaAhorro());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");

        MetaAhorro meta = existente.get();
        meta.setTitulo(dto.getTitulo());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setMontoActual(dto.getMontoActual());
        meta.setFechaLimite(dto.getFechaLimite());
        meta.setEstadoMetaAhorro(dto.getEstadoMetaAhorro());
        meta.setEstado(dto.getEstado());
        meta.setUsuario(dto.getIdUsuario());
        metaAhorroService.actualizar(meta);
        return ResponseEntity.ok("Meta de ahorro actualizada correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        if (metaAhorroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meta de ahorro no encontrada");
        }
        metaAhorroService.eliminar(id);
        return ResponseEntity.ok("Meta de ahorro eliminada correctamente");
    }

    private MetaAhorro toEntity(MetaAhorroDto dto) {
        MetaAhorro meta = new MetaAhorro();
        meta.setIdMetaAhorro(dto.getIdMetaAhorro());
        meta.setTitulo(dto.getTitulo());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setMontoActual(dto.getMontoActual());
        meta.setFechaLimite(dto.getFechaLimite());
        meta.setEstadoMetaAhorro(dto.getEstadoMetaAhorro());
        meta.setEstado(dto.getEstado());
        meta.setUsuario(dto.getIdUsuario());
        return meta;
    }

    private MetaAhorroDto toDto(MetaAhorro meta, ModelMapper mapper) {
        MetaAhorroDto dto = mapper.map(meta, MetaAhorroDto.class);
        dto.setIdUsuario(meta.getUsuario());
        return dto;
    }

    private String validar(MetaAhorroDto dto) {
        if (dto == null) return "Se requiere información de la meta";
        if (dto.getIdMetaAhorro() < 0) return "El id de la meta no puede ser negativo";
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) return "Se requiere un título";
        if (dto.getMontoObjetivo() == null || dto.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) return "El monto objetivo debe ser mayor que cero";
        if (dto.getMontoActual() == null || dto.getMontoActual().compareTo(BigDecimal.ZERO) < 0) return "El monto actual no puede ser negativo";
        if (dto.getFechaLimite() == null) return "Se requiere una fecha límite";
        if (dto.getEstadoMetaAhorro() == null || dto.getEstadoMetaAhorro().isBlank()) return "Se requiere el estado de la meta";
        if (dto.getIdUsuario() == null) return "Se requiere un usuario";
        return null;
    }
}
