package pe.edu.upc.walletix.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.UsuarioDesafioDTO;
import pe.edu.upc.walletix.entities.Desafio;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioDesafio;
import pe.edu.upc.walletix.servicesinterfaces.IDesafioService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioDesafioService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuariosdesafios")
public class UsuarioDesafioController {
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

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<UsuarioDesafioDTO>> listar() {
        ModelMapper modelMapper = crearModelMapper();
        List<UsuarioDesafioDTO> listaUsuarioDesafios = usuarioDesafioService.listar().stream()
                .map(usuarioDesafio -> convertirADTO(usuarioDesafio, modelMapper))
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaUsuarioDesafios);
    }

    @PostMapping("/web")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDesafioDTO usuarioDesafioDTO) {
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
        ModelMapper modelMapper = crearModelMapper();
        UsuarioDesafio nuevoUsuarioDesafio = modelMapper.map(usuarioDesafioDTO, UsuarioDesafio.class);
        nuevoUsuarioDesafio.setEstado(1);
        nuevoUsuarioDesafio.setUsuario(usuario.get());
        nuevoUsuarioDesafio.setDesafio(desafio.get());
        UsuarioDesafio usuarioDesafioRegistrado = usuarioDesafioService.registrar(nuevoUsuarioDesafio);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirADTO(usuarioDesafioRegistrado, modelMapper));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        Optional<UsuarioDesafio> usuarioDesafio = usuarioDesafioService.buscarPorId(id);
        if (usuarioDesafio.isPresent()) {
            return ResponseEntity.ok(convertirADTO(usuarioDesafio.get(), crearModelMapper()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
        }
    }

    @PutMapping("/actualiza")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioDesafioDTO usuarioDesafioDTO) {
        Optional<UsuarioDesafio> usuarioDesafioExistente = usuarioDesafioService.buscarPorId(usuarioDesafioDTO.getIdUsuarioDesafio());
        if (usuarioDesafioExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UsuarioDesafio> usuarioDesafio = usuarioDesafioService.buscarPorId(id);
        if (usuarioDesafio.isPresent()) {
            usuarioDesafioService.eliminar(id);
            return ResponseEntity.ok("Usuario desafío eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario desafío no encontrado");
        }
    }

    // Query nativo: desafíos de un usuario según su estado (US26). Ej: /usuariosdesafios/usuario/1?estadoDesafio=EN_PROGRESO
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorUsuarioYEstadoDesafio(@PathVariable int idUsuario,
                                                            @RequestParam String estadoDesafio) {
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
