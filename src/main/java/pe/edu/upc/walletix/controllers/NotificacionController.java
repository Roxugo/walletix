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
import pe.edu.upc.walletix.dtos.NotificacionDTO;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Notificaciones", description = "Notificaciones enviadas a los usuarios")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired
    private INotificacionService notificacionService;
    @Autowired
    private IUsuarioService usuarioService;

    private ModelMapper crearModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    // Con STRICT, ModelMapper no llena el id del usuario, así que se completa a mano
    private NotificacionDTO convertirADTO(Notificacion notificacion, ModelMapper modelMapper) {
        NotificacionDTO notificacionDTO = modelMapper.map(notificacion, NotificacionDTO.class);
        if (notificacion.getUsuario() != null) {
            notificacionDTO.setIdUsuario(notificacion.getUsuario().getIdUsuario());
        }
        return notificacionDTO;
    }

    @Operation(summary = "Listar las notificaciones activas")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<NotificacionDTO>> listar() {
        ModelMapper modelMapper = crearModelMapper();
        List<NotificacionDTO> listaNotificaciones = notificacionService.listar().stream()
                .map(notificacion -> convertirADTO(notificacion, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaNotificaciones);
    }

    @Operation(summary = "Registrar una notificación")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody NotificacionDTO notificacionDTO) {
        Optional<Usuario> usuario = usuarioService.listId(notificacionDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + notificacionDTO.getIdUsuario() + " no existe.");
        }
        ModelMapper modelMapper = crearModelMapper();
        Notificacion nuevaNotificacion = modelMapper.map(notificacionDTO, Notificacion.class);
        nuevaNotificacion.setEstado(1);
        nuevaNotificacion.setUsuario(usuario.get());
        Notificacion notificacionRegistrada = notificacionService.registrar(nuevaNotificacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(notificacionRegistrada, modelMapper));
    }

    @Operation(summary = "Buscar una notificación por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<Notificacion> notificacion = notificacionService.buscarPorId(id);
        if (notificacion.isPresent()) {
            return ResponseEntity.ok(convertirADTO(notificacion.get(), crearModelMapper()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificación no encontrada");
        }
    }

    @Operation(summary = "Actualizar una notificación (por ejemplo, marcarla como leída)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody NotificacionDTO notificacionDTO) {
        Optional<Notificacion> notificacionExistente = notificacionService.buscarPorId(notificacionDTO.getIdNotificacion());
        if (notificacionExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificación no encontrada");
        }
        Optional<Usuario> usuario = usuarioService.listId(notificacionDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + notificacionDTO.getIdUsuario() + " no existe.");
        }
        Notificacion notificacion = notificacionExistente.get();
        notificacion.setUsuario(usuario.get());
        notificacion.setTitulo(notificacionDTO.getTitulo());
        notificacion.setTipo(notificacionDTO.getTipo());
        notificacion.setMensaje(notificacionDTO.getMensaje());
        notificacion.setEstadoNotificacion(notificacionDTO.isEstadoNotificacion());
        notificacionService.actualizar(notificacion);
        return ResponseEntity.ok("Notificación actualizada correctamente");
    }

    @Operation(summary = "Eliminar una notificación (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<Notificacion> notificacion = notificacionService.buscarPorId(id);
        if (notificacion.isPresent()) {
            notificacionService.eliminar(id);
            return ResponseEntity.ok("Notificación eliminada correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Notificación no encontrada");
        }
    }

    // Query nativo: notificaciones no leídas de un usuario (US22, US24). Ej: /notificaciones/no-leidas/1
    @Operation(summary = "Listar las notificaciones no leídas de un usuario")
    @GetMapping("/no-leidas/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarNoLeidasPorUsuario(@PathVariable int idUsuario) {
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        ModelMapper modelMapper = crearModelMapper();
        List<NotificacionDTO> listaNotificaciones = notificacionService.buscarNoLeidasPorUsuario(idUsuario).stream()
                .map(notificacion -> convertirADTO(notificacion, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaNotificaciones);
    }

    // Query nativo: cantidad de notificaciones no leídas de un usuario. Ej: /notificaciones/no-leidas/1/cantidad
    @Operation(summary = "Contar las notificaciones no leídas de un usuario")
    @GetMapping("/no-leidas/{idUsuario}/cantidad")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> contarNoLeidasPorUsuario(@PathVariable int idUsuario) {
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        return ResponseEntity.ok(notificacionService.contarNoLeidasPorUsuario(idUsuario));
    }
}
