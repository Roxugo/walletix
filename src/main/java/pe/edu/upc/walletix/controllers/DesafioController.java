package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.DesafioDTO;
import pe.edu.upc.walletix.entities.Desafio;
import pe.edu.upc.walletix.servicesinterfaces.IDesafioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Desafíos", description = "Retos de ahorro disponibles en la plataforma")
@RestController
@RequestMapping("/desafios")
public class DesafioController {
    @Autowired
    private IDesafioService desafioService;

    private ModelMapper crearModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Operation(summary = "Listar los desafíos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<DesafioDTO>> listar() {
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.listar().stream()
                .map(desafio -> modelMapper.map(desafio, DesafioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDesafios);
    }

    @Operation(summary = "Registrar un desafío (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody DesafioDTO desafioDTO) {
        String error = validar(desafioDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        ModelMapper modelMapper = crearModelMapper();
        Desafio nuevoDesafio = modelMapper.map(desafioDTO, Desafio.class);
        nuevoDesafio.setEstado(1);
        Desafio desafioRegistrado = desafioService.registrar(nuevoDesafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(desafioRegistrado, DesafioDTO.class));
    }

    @Operation(summary = "Buscar un desafío por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        ModelMapper modelMapper = crearModelMapper();
        Optional<Desafio> desafio = desafioService.buscarPorId(id);
        if (desafio.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(desafio.get(), DesafioDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desafío no encontrado");
        }
    }

    @Operation(summary = "Actualizar los datos de un desafío (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody DesafioDTO desafioDTO) {
        Optional<Desafio> desafioExistente = desafioService.buscarPorId(desafioDTO.getIdDesafio());
        if (desafioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desafío no encontrado");
        }
        String error = validar(desafioDTO);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        Desafio desafio = desafioExistente.get();
        desafio.setTitulo(desafioDTO.getTitulo());
        desafio.setDescripcion(desafioDTO.getDescripcion());
        desafio.setMontoObjetivo(desafioDTO.getMontoObjetivo());
        desafio.setPuntosRecompensa(desafioDTO.getPuntosRecompensa());
        desafio.setFechaInicio(desafioDTO.getFechaInicio());
        desafio.setFechaFin(desafioDTO.getFechaFin());
        desafio.setSaldoInicial(desafioDTO.getSaldoInicial());
        desafio.setEdadMinima(desafioDTO.getEdadMinima());
        desafio.setSaldoProyectado(desafioDTO.getSaldoProyectado());
        desafioService.actualizar(desafio);
        return ResponseEntity.ok("Desafío actualizado correctamente");
    }

    @Operation(summary = "Eliminar un desafío (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Desafio> desafio = desafioService.buscarPorId(id);
        if (desafio.isPresent()) {
            // No se elimina un catálogo del que todavía dependen registros activos
            if (desafioService.tieneRegistrosActivos(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No se puede eliminar: el desafío tiene participantes activos");
            }
            desafioService.eliminar(id);
            return ResponseEntity.ok("Desafío eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desafío no encontrado");
        }
    }

    // Query nativo: desafíos vigentes (la fecha de hoy está dentro del rango). Ej: /desafios/vigentes
    @Operation(summary = "Listar los desafíos vigentes (la fecha de hoy está dentro del rango)")
    @GetMapping("/vigentes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<DesafioDTO>> buscarVigentes() {
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.buscarVigentes().stream()
                .map(desafio -> modelMapper.map(desafio, DesafioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDesafios);
    }

    // Query nativo: desafíos a los que puede acceder un usuario según su edad. Ej: /desafios/edad/20
    @Operation(summary = "Listar los desafíos a los que puede acceder un usuario según su edad")
    @GetMapping("/edad/{edad}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorEdadMinima(@PathVariable int edad) {
        if (edad < 0) {
            return ResponseEntity.badRequest().body("La edad no puede ser negativa");
        }
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.buscarPorEdadMinima(edad).stream()
                .map(desafio -> modelMapper.map(desafio, DesafioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDesafios);
    }

    private String validar(DesafioDTO desafioDTO) {
        if (desafioDTO.getTitulo() == null || desafioDTO.getTitulo().isBlank()) {
            return "El título es obligatorio";
        }
        if (desafioDTO.getDescripcion() == null || desafioDTO.getDescripcion().isBlank()) {
            return "La descripción es obligatoria";
        }
        if (desafioDTO.getPuntosRecompensa() < 0) {
            return "Los puntos de recompensa no pueden ser negativos";
        }
        if (desafioDTO.getEdadMinima() < 0) {
            return "La edad mínima no puede ser negativa";
        }
        if (desafioDTO.getFechaInicio() == null || desafioDTO.getFechaFin() == null) {
            return "Las fechas de inicio y fin son obligatorias";
        }
        if (desafioDTO.getMontoObjetivo() == null || desafioDTO.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {
            return "El monto objetivo debe ser mayor a 0";
        }
        if (desafioDTO.getFechaInicio() != null && desafioDTO.getFechaFin() != null
                && desafioDTO.getFechaFin().isBefore(desafioDTO.getFechaInicio())) {
            return "La fecha fin no puede ser anterior a la fecha inicio";
        }
        return null;
    }
}
