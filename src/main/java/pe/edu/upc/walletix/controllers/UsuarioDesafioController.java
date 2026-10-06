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
import pe.edu.upc.walletix.dtos.UsuarioDesafioDTO;
import pe.edu.upc.walletix.entities.Desafio;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioDesafio;
import pe.edu.upc.walletix.servicesinterfaces.IDesafioService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioDesafioService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Desafíos de usuario", description = "Participación de los usuarios en los desafíos")
@RestController
@RequestMapping("/usuariosdesafios")
public class UsuarioDesafioController {
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private IUsuarioDesafioService usuarioDesafioService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IDesafioService desafioService;

    private ModelMapper crearModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    // Con STRICT, ModelMapper no llena los ids del usuario y del desafío, así que se completan a mano
    private UsuarioDesafioDTO convertirADTO(UsuarioDesafio usuarioDesafio, ModelMapper modelMapper) {
        UsuarioDesafioDTO usuarioDesafioDTO = modelMapper.map(usuarioDesafio, UsuarioDesafioDTO.class);
        if (usuarioDesafio.getUsuario() != null) {
            usuarioDesafioDTO.setIdUsuario(usuarioDesafio.getUsuario().getIdUsuario());
        }
        if (usuarioDesafio.getDesafio() != null) {
            usuarioDesafioDTO.setIdDesafio(usuarioDesafio.getDesafio().getIdDesafio());
        }
        return usuarioDesafioDTO;
    }

    @Operation(summary = "Listar las participaciones en desafíos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<UsuarioDesafioDTO>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = crearModelMapper();
        List<UsuarioDesafioDTO> listaUsuarioDesafios = usuarioDesafioService.listar().stream()
                .filter(usuarioDesafio -> esAdmin || usuarioDesafio.getUsuario().getIdUsuario() == idActual)
                .map(usuarioDesafio -> convertirADTO(usuarioDesafio, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaUsuarioDesafios);
    }

    @Operation(summary = "Inscribir a un usuario en un desafío")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDesafioDTO usuarioDesafioDTO, Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, usuarioDesafioDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        Optional<Usuario> usuario = usuarioService.listId(usuarioDesafioDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + usuarioDesafioDTO.getIdUsuario() + " no existe.");
        }
        Optional<Desafio> desafio = desafioService.buscarPorId(usuarioDesafioDTO.getIdDesafio());
        if (desafio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El desafío con ID " + usuarioDesafioDTO.getIdDesafio() + " no existe.");
        }
        if (!porcentajeValido(usuarioDesafioDTO.getPorcentajeProgreso())) {
            return ResponseEntity.badRequest().body("El porcentaje debe estar entre 0 y 100");
        }
        if (usuarioDesafioDTO.getEstadoDesafio() == null || usuarioDesafioDTO.getEstadoDesafio().isBlank()) {
            return ResponseEntity.badRequest().body("El estado del desafío es obligatorio");
        }
        ModelMapper modelMapper = crearModelMapper();
        UsuarioDesafio nuevoUsuarioDesafio = modelMapper.map(usuarioDesafioDTO, UsuarioDesafio.class);
        nuevoUsuarioDesafio.setEstado(1);
        nuevoUsuarioDesafio.setUsuario(usuario.get());
        nuevoUsuarioDesafio.setDesafio(desafio.get());
        UsuarioDesafio usuarioDesafioRegistrado = usuarioDesafioService.registrar(nuevoUsuarioDesafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(usuarioDesafioRegistrado, modelMapper));
    }

    @Operation(summary = "Buscar una participación por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        Optional<UsuarioDesafio> usuarioDesafio = usuarioDesafioService.buscarPorId(id);
        if (usuarioDesafio.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, usuarioDesafio.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            return ResponseEntity.ok(convertirADTO(usuarioDesafio.get(), crearModelMapper()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
        }
    }

    @Operation(summary = "Actualizar el progreso de un usuario en un desafío")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDesafioDTO usuarioDesafioDTO, Authentication autenticacion) {
        Optional<UsuarioDesafio> usuarioDesafioExistente = usuarioDesafioService.buscarPorId(usuarioDesafioDTO.getIdUsuarioDesafio());
        if (usuarioDesafioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
        }
        if (!usuarioActual.puedeGestionar(autenticacion, usuarioDesafioExistente.get().getUsuario().getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        if (!usuarioActual.puedeGestionar(autenticacion, usuarioDesafioDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        Optional<Usuario> usuario = usuarioService.listId(usuarioDesafioDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + usuarioDesafioDTO.getIdUsuario() + " no existe.");
        }
        Optional<Desafio> desafio = desafioService.buscarPorId(usuarioDesafioDTO.getIdDesafio());
        if (desafio.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El desafío con ID " + usuarioDesafioDTO.getIdDesafio() + " no existe.");
        }
        if (!porcentajeValido(usuarioDesafioDTO.getPorcentajeProgreso())) {
            return ResponseEntity.badRequest().body("El porcentaje debe estar entre 0 y 100");
        }
        if (usuarioDesafioDTO.getEstadoDesafio() == null || usuarioDesafioDTO.getEstadoDesafio().isBlank()) {
            return ResponseEntity.badRequest().body("El estado del desafío es obligatorio");
        }
        UsuarioDesafio usuarioDesafio = usuarioDesafioExistente.get();
        usuarioDesafio.setUsuario(usuario.get());
        usuarioDesafio.setDesafio(desafio.get());
        usuarioDesafio.setSaldoInicial(usuarioDesafioDTO.getSaldoInicial());
        usuarioDesafio.setMontoProgresoActual(usuarioDesafioDTO.getMontoProgresoActual());
        usuarioDesafio.setPorcentajeProgreso(usuarioDesafioDTO.getPorcentajeProgreso());
        usuarioDesafio.setEstadoDesafio(usuarioDesafioDTO.getEstadoDesafio());
        usuarioDesafioService.actualizar(usuarioDesafio);
        return ResponseEntity.ok("Usuario desafío actualizado correctamente");
    }

    @Operation(summary = "Eliminar una participación (borrado lógico)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id, Authentication autenticacion) {
        Optional<UsuarioDesafio> usuarioDesafio = usuarioDesafioService.buscarPorId(id);
        if (usuarioDesafio.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, usuarioDesafio.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            usuarioDesafioService.eliminar(id);
            return ResponseEntity.ok("Usuario desafío eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
        }
    }

    // Query nativo: desafíos de un usuario según su estado (US26). Ej: /usuariosdesafios/usuario/1?estadoDesafio=EN_PROGRESO
    @Operation(summary = "Listar los desafíos de un usuario según su estado")
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorUsuarioYEstadoDesafio(@PathVariable int idUsuario,
                                                            @RequestParam String estadoDesafio,
                                                            Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        ModelMapper modelMapper = crearModelMapper();
        List<UsuarioDesafioDTO> listaUsuarioDesafios = usuarioDesafioService.buscarPorUsuarioYEstadoDesafio(idUsuario, estadoDesafio).stream()
                .map(usuarioDesafio -> convertirADTO(usuarioDesafio, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaUsuarioDesafios);
    }

    // Query nativo: cantidad de usuarios inscritos en un desafío. Ej: /usuariosdesafios/desafio/1/cantidad
    @Operation(summary = "Contar cuántos usuarios participan en un desafío")
    @GetMapping("/desafio/{idDesafio}/cantidad")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> contarUsuariosPorDesafio(@PathVariable int idDesafio) {
        if (desafioService.buscarPorId(idDesafio).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Desafío no encontrado");
        }
        return ResponseEntity.ok(usuarioDesafioService.contarUsuariosPorDesafio(idDesafio));
    }

    private boolean porcentajeValido(BigDecimal porcentaje) {
        return porcentaje != null
                && porcentaje.compareTo(BigDecimal.ZERO) >= 0
                && porcentaje.compareTo(new BigDecimal("100")) <= 0;
    }
}
