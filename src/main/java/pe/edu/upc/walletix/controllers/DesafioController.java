package pe.edu.upc.walletix.controllers;

import pe.edu.upc.walletix.securities.UsuarioActual;
import org.springframework.security.core.Authentication;
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
import pe.edu.upc.walletix.i18n.Mensajes;
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
    private Mensajes mensajes;
    @Autowired
    private IDesafioService desafioService;
    @Autowired
    private UsuarioActual usuarioActual;

    private ModelMapper crearModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Operation(summary = "Listar los desafíos activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<DesafioDTO>> listar(Authentication autenticacion) {
        // Los retos oficiales los ve todo usuario; los personales (US29) solo su creador. Un ADMIN ve todos.
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.listar().stream()
                .filter(desafio -> esAdmin || desafio.esOficial() || desafio.getUsuarioCreador().getIdUsuario() == idActual)
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
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        ModelMapper modelMapper = crearModelMapper();
        Optional<Desafio> desafio = desafioService.buscarPorId(id);
        if (desafio.isPresent()) {
            // Un reto personal solo lo ve su creador (o un ADMIN)
            if (!desafio.get().esOficial() && !usuarioActual.puedeGestionar(autenticacion, desafio.get().getUsuarioCreador().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
            }
            return ResponseEntity.ok(modelMapper.map(desafio.get(), DesafioDTO.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("desafio.no.encontrado"));
        }
    }

    @Operation(summary = "Actualizar los datos de un desafío (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody DesafioDTO desafioDTO) {
        Optional<Desafio> desafioExistente = desafioService.buscarPorId(desafioDTO.getIdDesafio());
        if (desafioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("desafio.no.encontrado"));
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
        desafio.setDuracionDias(desafioDTO.getDuracionDias());
        desafio.setTipoDesafio(desafioDTO.getTipoDesafio());
        desafioService.actualizar(desafio);
        return ResponseEntity.ok(mensajes.get("desafio.actualizado.correctamente"));
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
                        .body(mensajes.get("no.puede.eliminar.desafio.tiene"));
            }
            desafioService.eliminar(id);
            return ResponseEntity.ok(mensajes.get("desafio.eliminado.correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("desafio.no.encontrado"));
        }
    }

    // Query nativo: desafíos vigentes (la fecha de hoy está dentro del rango). Ej: /desafios/vigentes
    @Operation(summary = "Listar los desafíos vigentes (la fecha de hoy está dentro del rango)")
    @GetMapping("/vigentes")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<DesafioDTO>> buscarVigentes() {
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.buscarVigentes().stream()
                .filter(Desafio::esOficial) // los personales no son "retos de la plataforma"
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
            return ResponseEntity.badRequest().body(mensajes.get("edad.no.puede.ser.negativa"));
        }
        ModelMapper modelMapper = crearModelMapper();
        List<DesafioDTO> listaDesafios = desafioService.buscarPorEdadMinima(edad).stream()
                .filter(Desafio::esOficial)
                .map(desafio -> modelMapper.map(desafio, DesafioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaDesafios);
    }

    private String validar(DesafioDTO desafioDTO) {
        if (desafioDTO.getTitulo() == null || desafioDTO.getTitulo().isBlank()) {
            return mensajes.get("titulo.obligatorio");
        }
        if (desafioDTO.getDescripcion() == null || desafioDTO.getDescripcion().isBlank()) {
            return mensajes.get("descripcion.obligatoria");
        }
        if (desafioDTO.getPuntosRecompensa() < 0) {
            return mensajes.get("puntos.recompensa.no.pueden.ser");
        }
        if (desafioDTO.getEdadMinima() < 0) {
            return mensajes.get("edad.minima.no.puede.ser");
        }
        if (desafioDTO.getFechaInicio() == null || desafioDTO.getFechaFin() == null) {
            return mensajes.get("fechas.inicio.fin.son.obligatorias");
        }
        if (desafioDTO.getMontoObjetivo() == null || desafioDTO.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {
            return mensajes.get("monto.objetivo.debe.ser.mayor");
        }
        if (desafioDTO.getFechaInicio() != null && desafioDTO.getFechaFin() != null
                && desafioDTO.getFechaFin().isBefore(desafioDTO.getFechaInicio())) {
            return mensajes.get("fecha.fin.no.puede.ser");
        }
        // La duración (si se envía) debe caber entre la fecha de inicio y la de fin
        if (desafioDTO.getDuracionDias() != null) {
            long diasDelPeriodo = java.time.temporal.ChronoUnit.DAYS.between(desafioDTO.getFechaInicio(), desafioDTO.getFechaFin()) + 1;
            if (desafioDTO.getDuracionDias() < 1 || desafioDTO.getDuracionDias() > diasDelPeriodo) {
                return mensajes.get("reto.duracion.no.cabe", String.valueOf(diasDelPeriodo));
            }
        }
        return null;
    }
}
