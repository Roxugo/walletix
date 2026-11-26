package pe.edu.upc.walletix.controllers;

import pe.edu.upc.walletix.servicesinterfaces.IGamificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.MetaAhorroDto;
import pe.edu.upc.walletix.entities.MetaAhorro;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.MetaAhorroServiceInterface;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Metas de ahorro", description = "Metas de ahorro del usuario")
@RestController
@RequestMapping("/metas-ahorro")
public class MetaAhorroController {
    // Logros automáticos (Primer gasto registrado, Alcancía Iniciada...): se revisan después de registrar
    @Autowired
    private IGamificacionService gamificacionService;
    @Autowired
    private Mensajes mensajes;
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private MetaAhorroServiceInterface metaAhorroService;

    @Autowired
    private IUsuarioService usuarioService;

    @Operation(summary = "Listar las metas de ahorro activas")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<MetaAhorroDto>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper mapper = new ModelMapper();
        List<MetaAhorroDto> lista = metaAhorroService.listar().stream()
                .filter(meta -> esAdmin || meta.getUsuario().getIdUsuario() == idActual)
                .map(meta -> toDto(meta, mapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Registrar una meta de ahorro")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody MetaAhorroDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);
        // Solo al crear: una meta nueva no puede vencer en el pasado (al editar se permite, por las metas antiguas)
        if (dto.getFechaLimite().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest().body(mensajes.get("fecha.limite.no.puede.ser.pasada"));
        }
        if (dto.getMontoActual().compareTo(dto.getMontoObjetivo()) >= 0) {
            return ResponseEntity.badRequest().body(mensajes.get("meta.nueva.ya.cumplida"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));

        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        MetaAhorro meta = toEntity(dto);
        meta.setUsuario(usuario.get());
        MetaAhorro registrada = metaAhorroService.registrar(meta);
        gamificacionService.revisarLogros(usuario.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(registrada, new ModelMapper()));
    }

    @Operation(summary = "Buscar una meta de ahorro por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        Optional<MetaAhorro> meta = metaAhorroService.buscarPorId(id);
        if (meta.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("meta.ahorro.no.encontrada"));
        if (!usuarioActual.puedeGestionar(autenticacion, meta.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        return ResponseEntity.ok(toDto(meta.get(), new ModelMapper()));
    }

    @Operation(summary = "Actualizar los datos de una meta de ahorro")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody MetaAhorroDto dto, Authentication autenticacion) {
        String error = validar(dto);
        if (error != null) return ResponseEntity.badRequest().body(error);

        Optional<MetaAhorro> existente = metaAhorroService.buscarPorId(dto.getIdMetaAhorro());
        if (existente.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("meta.ahorro.no.encontrada"));
        // Debe ser dueño del registro y no puede pasárselo a otro usuario
        if (!usuarioActual.puedeGestionar(autenticacion, existente.get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        if (!usuarioActual.puedeGestionar(autenticacion, dto.getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        Optional<Usuario> usuario = usuarioService.listId(dto.getIdUsuario());
        if (usuario.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));

        MetaAhorro meta = existente.get();
        meta.setTitulo(dto.getTitulo());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setMontoActual(dto.getMontoActual());
        meta.setFechaLimite(dto.getFechaLimite());
        meta.setEstadoMetaAhorro(estadoSegunMonto(dto));
        meta.setUsuario(usuario.get());
        metaAhorroService.actualizar(meta);
        gamificacionService.revisarLogros(usuario.get()); // al cumplir una meta: "Gran ahorrador"
        return ResponseEntity.ok(mensajes.get("meta.ahorro.actualizada.correctamente"));
    }

    @Operation(summary = "Eliminar una meta de ahorro (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        if (metaAhorroService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("meta.ahorro.no.encontrada"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, metaAhorroService.buscarPorId(id).get().getUsuario().getIdUsuario())) return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        metaAhorroService.eliminar(id);
        return ResponseEntity.ok(mensajes.get("meta.ahorro.eliminada.correctamente"));
    }

    private MetaAhorro toEntity(MetaAhorroDto dto) {
        MetaAhorro meta = new MetaAhorro();
        meta.setIdMetaAhorro(dto.getIdMetaAhorro());
        meta.setTitulo(dto.getTitulo());
        meta.setMontoObjetivo(dto.getMontoObjetivo());
        meta.setMontoActual(dto.getMontoActual());
        meta.setFechaLimite(dto.getFechaLimite());
        meta.setEstadoMetaAhorro(dto.getEstadoMetaAhorro());
        meta.setEstado(1); // Todo registro nuevo empieza activo
        return meta;
    }

    private MetaAhorroDto toDto(MetaAhorro meta, ModelMapper mapper) {
        MetaAhorroDto dto = mapper.map(meta, MetaAhorroDto.class);
        if (meta.getUsuario() != null) dto.setIdUsuario(meta.getUsuario().getIdUsuario());
        // Las metas antiguas guardaron "EN_PROGRESO" / "COMPLETADA": la app usa activa / cumplida / pausada
        dto.setEstadoMetaAhorro(normalizarEstado(meta.getEstadoMetaAhorro()));
        return dto;
    }

    // "EN_PROGRESO" -> "activa", "COMPLETADA" -> "cumplida"; cualquier otro valor queda en minúsculas
    private static String normalizarEstado(String estado) {
        if (estado == null) return null;
        String valor = estado.trim().toLowerCase();
        return switch (valor) {
            case "en_progreso", "en progreso" -> "activa";
            case "completada" -> "cumplida";
            default -> valor;
        };
    }

    // Al llegar al objetivo la meta pasa sola a "cumplida" (antes quedaba "En progreso" con el 100%)
    private String estadoSegunMonto(MetaAhorroDto dto) {
        if (dto.getMontoActual().compareTo(dto.getMontoObjetivo()) >= 0) return "cumplida";
        return normalizarEstado(dto.getEstadoMetaAhorro());
    }

    private String validar(MetaAhorroDto dto) {
        if (dto == null) return mensajes.get("requiere.informacion.meta");
        if (dto.getIdMetaAhorro() < 0) return mensajes.get("id.meta.no.puede.ser");
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) return mensajes.get("requiere.titulo");
        if (dto.getMontoObjetivo() == null || dto.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) return mensajes.get("monto.objetivo.debe.ser.mayor.2");
        if (dto.getMontoActual() == null || dto.getMontoActual().compareTo(BigDecimal.ZERO) < 0) return mensajes.get("monto.actual.no.puede.ser");
        if (dto.getFechaLimite() == null) return mensajes.get("requiere.fecha.limite");
        if (dto.getEstadoMetaAhorro() == null || dto.getEstadoMetaAhorro().isBlank()) return mensajes.get("requiere.estado.meta");
        String estado = normalizarEstado(dto.getEstadoMetaAhorro());
        if (!List.of("activa", "cumplida", "pausada").contains(estado)) return mensajes.get("estado.meta.no.valido");
        dto.setEstadoMetaAhorro(estado);
        // Soles y céntimos
        if (dto.getMontoObjetivo().stripTrailingZeros().scale() > 2 || dto.getMontoActual().stripTrailingZeros().scale() > 2) {
            return mensajes.get("monto.maximo.dos.decimales");
        }
        // Lo ahorrado no puede pasar el objetivo (antes se aceptaba S/ 1,200 de S/ 1,000)
        if (dto.getMontoActual().compareTo(dto.getMontoObjetivo()) > 0) return mensajes.get("meta.ahorrado.supera.objetivo");
        // "Cumplida" solo cuando de verdad se llegó al objetivo (si no, se ganaba "Gran ahorrador" sin ahorrar)
        if ("cumplida".equals(estado) && dto.getMontoActual().compareTo(dto.getMontoObjetivo()) < 0) {
            return mensajes.get("meta.cumplida.sin.objetivo");
        }
        if (dto.getIdUsuario() <= 0) return mensajes.get("requiere.id.usuario.valido");
        return null;
    }
}
