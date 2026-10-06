package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.IntentoQuizUsuarioDTO;
import pe.edu.upc.walletix.dtos.ProgresoAprendizajeDTO;
import pe.edu.upc.walletix.entities.IntentoQuizUsuario;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IIntentoQuizUsuarioService;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Intentos de quiz", description = "Intentos de los usuarios al rendir el quiz de una microlección")
@RestController
@RequestMapping("/intentos-quiz")
public class IntentoQuizUsuarioController {
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private IIntentoQuizUsuarioService intentoQuizUsuarioService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IMicroleccionService microleccionService;

    @Operation(summary = "Listar los intentos de quiz activos")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<IntentoQuizUsuarioDTO>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper modelMapper = new ModelMapper();
        List<IntentoQuizUsuarioDTO> listaIntentos = intentoQuizUsuarioService.list().stream()
                .filter(intento -> esAdmin || intento.getUsuario().getIdUsuario() == idActual)
                .map(intentoQuizUsuario -> modelMapper.map(intentoQuizUsuario, IntentoQuizUsuarioDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaIntentos);
    }

    @Operation(summary = "Registrar un intento de quiz (calcula si aprobó)")
    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody IntentoQuizUsuarioDTO intentoQuizUsuarioDTO, Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, intentoQuizUsuarioDTO.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        if (intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() < 0 || intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() > 100) {
            return ResponseEntity.badRequest()
                    .body("El puntaje debe estar entre 0 y 100");
        }
        Optional<Usuario> usuario = usuarioService.listId(intentoQuizUsuarioDTO.getIdUsuario());
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        Optional<Microleccion> microleccion = microleccionService.listId(intentoQuizUsuarioDTO.getIdMicroleccion());
        if (microleccion.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Microlección no encontrada");
        }
        if (!microleccion.get().getTipoMicroleccion().equals("leccion")) {
            return ResponseEntity.badRequest()
                    .body("Solo las microlecciones de tipo leccion tienen quiz");
        }

        ModelMapper modelMapper = new ModelMapper();
        IntentoQuizUsuario nuevoIntento = modelMapper.map(intentoQuizUsuarioDTO, IntentoQuizUsuario.class);
        nuevoIntento.setUsuario(usuario.get());
        nuevoIntento.setMicroleccion(microleccion.get());
        // Aprueba si su puntaje llega al puntaje aprobatorio de la microlección
        nuevoIntento.setAprobadoIntentoQuizUsuario(intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() >= microleccion.get().getPuntajeAprobatorioMicroleccion());
        IntentoQuizUsuario intentoRegistrado = intentoQuizUsuarioService.insert(nuevoIntento);
        IntentoQuizUsuarioDTO respuestaDTO = modelMapper.map(intentoRegistrado, IntentoQuizUsuarioDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuestaDTO);
    }

    @Operation(summary = "Buscar un intento de quiz por su id")
    @GetMapping("/{idIntentoQuizUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int idIntentoQuizUsuario, Authentication autenticacion) {
        ModelMapper modelMapper = new ModelMapper();
        Optional<IntentoQuizUsuario> intentoQuizUsuario = intentoQuizUsuarioService.listId(idIntentoQuizUsuario);
        if (intentoQuizUsuario.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, intentoQuizUsuario.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            IntentoQuizUsuarioDTO intentoQuizUsuarioDTO = modelMapper.map(intentoQuizUsuario.get(), IntentoQuizUsuarioDTO.class);
            return ResponseEntity.ok(intentoQuizUsuarioDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
    }

    @Operation(summary = "Corregir el puntaje de un intento de quiz (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody IntentoQuizUsuarioDTO intentoQuizUsuarioDTO) {
        if (intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() < 0 || intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() > 100) {
            return ResponseEntity.badRequest()
                    .body("El puntaje debe estar entre 0 y 100");
        }
        Optional<IntentoQuizUsuario> intentoExistente = intentoQuizUsuarioService.listId(intentoQuizUsuarioDTO.getIdIntentoQuizUsuario());
        if (intentoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
        IntentoQuizUsuario intentoQuizUsuario = intentoExistente.get();
        intentoQuizUsuario.setPuntajeIntentoQuizUsuario(intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario());
        intentoQuizUsuario.setAprobadoIntentoQuizUsuario(intentoQuizUsuarioDTO.getPuntajeIntentoQuizUsuario() >= intentoQuizUsuario.getMicroleccion().getPuntajeAprobatorioMicroleccion());
        intentoQuizUsuarioService.update(intentoQuizUsuario);
        return ResponseEntity.ok("Intento actualizado correctamente");
    }

    @Operation(summary = "Eliminar un intento de quiz (borrado lógico)")
    @DeleteMapping("/{idIntentoQuizUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int idIntentoQuizUsuario, Authentication autenticacion) {
        Optional<IntentoQuizUsuario> intentoQuizUsuario = intentoQuizUsuarioService.listId(idIntentoQuizUsuario);
        if (intentoQuizUsuario.isPresent()) {
            if (!usuarioActual.puedeGestionar(autenticacion, intentoQuizUsuario.get().getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            intentoQuizUsuarioService.delete(idIntentoQuizUsuario);
            return ResponseEntity.ok("Intento eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Intento no encontrado");
        }
    }

    // Query nativo: progreso de aprendizaje del usuario. Ej: /intentos-quiz/progreso/1
    @Operation(summary = "Ver el progreso de aprendizaje de un usuario")
    @GetMapping("/progreso/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> progreso(@PathVariable int idUsuario, Authentication autenticacion) {
        if (!usuarioActual.puedeGestionar(autenticacion, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
        }
        if (usuarioService.listId(idUsuario).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        List<Object[]> filasProgreso = intentoQuizUsuarioService.progresoPorUsuario(idUsuario);
        List<ProgresoAprendizajeDTO> listaProgreso = new ArrayList<>();
        for (Object[] fila : filasProgreso) {
            ProgresoAprendizajeDTO progresoDTO = new ProgresoAprendizajeDTO();
            progresoDTO.setIdMicroleccion(((Number) fila[0]).intValue());
            progresoDTO.setTituloMicroleccion((String) fila[1]);
            progresoDTO.setCantidadIntentos(((Number) fila[2]).intValue());
            progresoDTO.setMejorPuntaje(((Number) fila[3]).intValue());
            progresoDTO.setAprobado((Boolean) fila[4]);
            listaProgreso.add(progresoDTO);
        }
        return ResponseEntity.ok(listaProgreso);
    }
}
