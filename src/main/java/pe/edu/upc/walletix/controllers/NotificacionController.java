package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.NotificacionDTO;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Notificaciones", description = "Notificaciones enviadas a los usuarios")
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {
    @Autowired
    private Mensajes mensajes;
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private INotificacionService notificacionService;
    @Autowired
    private IUsuarioService usuarioService;

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String validar(NotificacionDTO dto) {
        if (estaVacio(dto.getTitulo())) {
            return mensajes.get("titulo.obligatorio");
        }
        if (estaVacio(dto.getTipo())) {
            return mensajes.get("tipo.obligatorio");
        }
        if (estaVacio(dto.getMensaje())) {
            return mensajes.get("mensaje.obligatorio");
        }
        return null;
    }

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
    public ResponseEntity<List<NotificacionDTO>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = crearModelMapper();
        List<NotificacionDTO> listaNotificaciones = notificacionService.listar().stream()
                .filter(notificacion -> esAdmin || notificacion.getUsuario().getIdUsuario() == idActual)
                .map(notificacion -> convertirADTO(notificacion, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaNotificaciones);
    }

    @Operation(summary = "Registrar una notificación")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody NotificacionDTO notificacionDTO, Authentication autenticacion) {
        String errorTexto = validar(notificacionDTO);
        if (errorTexto != null) {
            return ResponseEntity.badRequest().body(errorTexto);
        }
        if (!usuarioActual.puedeGestionar(autenticacion, notificacionDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        Optional<Usuario> usuario = usuarioService.listId(notificacionDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("usuario.id.no.existe", String.valueOf(notificacionDTO.getIdUsuario())));
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
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        Optional<Notificacion> notificacion = notificacionService.buscarPorId(id);
        if (notificacion.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, notificacion.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
            }
            return ResponseEntity.ok(convertirADTO(notificacion.get(), crearModelMapper()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("notificacion.no.encontrada"));
        }
    }

    @Operation(summary = "Actualizar una notificación (por ejemplo, marcarla como leída)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody NotificacionDTO notificacionDTO, Authentication autenticacion) {
        String errorTexto = validar(notificacionDTO);
        if (errorTexto != null) {
            return ResponseEntity.badRequest().body(errorTexto);
        }
        Optional<Notificacion> notificacionExistente = notificacionService.buscarPorId(notificacionDTO.getIdNotificacion());
        if (notificacionExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("notificacion.no.encontrada"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, notificacionExistente.get().getUsuario().getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, notificacionDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        Optional<Usuario> usuario = usuarioService.listId(notificacionDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("usuario.id.no.existe", String.valueOf(notificacionDTO.getIdUsuario())));
        }
        Notificacion notificacion = notificacionExistente.get();
        notificacion.setUsuario(usuario.get());
        notificacion.setTitulo(notificacionDTO.getTitulo());
        notificacion.setTipo(notificacionDTO.getTipo());
        notificacion.setMensaje(notificacionDTO.getMensaje());
        notificacion.setEstadoNotificacion(notificacionDTO.isEstadoNotificacion());
        notificacionService.actualizar(notificacion);
        return ResponseEntity.ok(mensajes.get("notificacion.actualizada.correctamente"));
    }

    // US24: marcar UNA notificación como leída (leida=true) o como no leída (leida=false).
    // Solo cambia ese dato, así el frontend no tiene que reenviar título y mensaje.
    @Operation(summary = "Marcar una notificación como leída o no leída (US24)")
    @PutMapping("/{id}/leida")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> marcarLeida(@PathVariable int id, @RequestParam boolean leida, Authentication autenticacion) {
        Optional<Notificacion> notificacion = notificacionService.buscarPorId(id);
        if (notificacion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("notificacion.no.encontrada"));
        }
        if (!usuarioActual.puedeGestionar(autenticacion, notificacion.get().getUsuario().getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        notificacion.get().setEstadoNotificacion(leida);
        notificacionService.actualizar(notificacion.get());
        return ResponseEntity.ok(convertirADTO(notificacion.get(), crearModelMapper()));
    }

    // US24: "Marcar todas como leídas" del usuario que inició sesión (sale del token, no de la URL)
    @Operation(summary = "Marcar como leídas todas mis notificaciones (US24)")
    @PutMapping("/leer-todas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<Map<String, Integer>> marcarTodasLeidas(Authentication autenticacion) {
        int marcadas = notificacionService.marcarTodasComoLeidas(usuarioActual.id(autenticacion));
        return ResponseEntity.ok(Map.of("marcadas", marcadas));
    }

    @Operation(summary = "Eliminar una notificación (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        Optional<Notificacion> notificacion = notificacionService.buscarPorId(id);
        if (notificacion.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, notificacion.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
            }
            notificacionService.eliminar(id);
            return ResponseEntity.ok(mensajes.get("notificacion.eliminada.correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mensajes.get("notificacion.no.encontrada"));
        }
    }

    // Query nativo: notificaciones no leídas de un usuario (US22, US24). Ej: /notificaciones/no-leidas/1
    @Operation(summary = "Listar las notificaciones no leídas de un usuario")
    @GetMapping("/no-leidas/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarNoLeidasPorUsuario(@PathVariable int idUsuario, Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
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
    public ResponseEntity<?> contarNoLeidasPorUsuario(@PathVariable int idUsuario, Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(mensajes.get("no.tiene.permiso.sobre.datos"));
        }
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        }
        return ResponseEntity.ok(notificacionService.contarNoLeidasPorUsuario(idUsuario));
    }
}
