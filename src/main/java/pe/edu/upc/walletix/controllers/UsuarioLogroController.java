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
import pe.edu.upc.walletix.dtos.LogroPopularidadDTO;
import pe.edu.upc.walletix.dtos.UsuarioLogroDTO;
import pe.edu.upc.walletix.dtos.UsuarioLogrosCountDTO;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.entities.UsuarioLogro;
import pe.edu.upc.walletix.servicesinterfaces.ILogroService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioLogroService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.securities.UsuarioActual;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Logros de usuario", description = "Logros obtenidos por cada usuario")
@RestController
@RequestMapping("/usuarioslogros")
public class UsuarioLogroController {
    // Usuario que inició sesión: un USUARIO solo trabaja con sus datos, un ADMIN con todos
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private IUsuarioLogroService usuariologroService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ILogroService logroService;

    @Operation(summary = "Listar los logros obtenidos por los usuarios")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<UsuarioLogroDTO>> listar(Authentication autenticacion) {
        // Un USUARIO solo ve sus propios registros; un ADMIN ve todos
        boolean esAdmin = usuarioActual.esAdmin(autenticacion);
        int idActual = usuarioActual.id(autenticacion);
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);

        List<UsuarioLogroDTO> listalogros = usuariologroService.list().stream()
                .filter(y -> esAdmin || y.getUsuario().getIdUsuario() == idActual)
                .map(y -> {
                    UsuarioLogroDTO dto = m.map(y, UsuarioLogroDTO.class);
                    // Asignamos los IDs de las claves foráneas
                    if (y.getUsuario() != null) dto.setIdUsuario(y.getUsuario().getIdUsuario());
                    if (y.getLogro() != null) dto.setIdLogro(y.getLogro().getIdLogro());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(listalogros);
    }
    @Operation(summary = "Asignar un logro a un usuario (solo ADMIN)")
    @PostMapping("/web")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> registrar(@RequestBody UsuarioLogroDTO dto) {
        // 1. Validar que el Usuario foráneo exista y esté activo (si no existe, responde 404)
        Optional<Usuario> usuarioOpt = usuarioService.listId(dto.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + dto.getIdUsuario() + " no existe.");
        }

        // 2. Validar que el Logro foráneo exista y esté activo (si no existe, responde 404)
        Optional<Logro> logroOpt = logroService.listId(dto.getIdLogro());
        if (logroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El logro con ID " + dto.getIdLogro() + " no existe.");
        }

        ModelMapper m = new ModelMapper();
        // 3. Estrategia STRICT: obliga a coincidir nombres exactos al 100% y evita que
        //    ModelMapper confunda idUsuarioLogro con idLogro o idUsuario (evita error 500)
        m.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);
        UsuarioLogro c = m.map(dto, UsuarioLogro.class);
        c.setEstadoUsuarioLogro(1);
        c.setUsuario(usuarioOpt.get());
        c.setLogro(logroOpt.get());
        UsuarioLogro cur = usuariologroService.insert(c);
        UsuarioLogroDTO responseDTO = m.map(cur, UsuarioLogroDTO.class);
        responseDTO.setIdUsuario(cur.getUsuario().getIdUsuario());
        responseDTO.setIdLogro(cur.getLogro().getIdLogro());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    @Operation(summary = "Buscar un logro de usuario por su id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<?> buscarPorId(@PathVariable int id, Authentication autenticacion) {
        ModelMapper m = new ModelMapper();
        m.getConfiguration().setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STRICT);

        Optional<UsuarioLogro> mach = usuariologroService.listId(id);
        if (mach.isPresent()) {
            UsuarioLogro ul = mach.get();
            if (!usuarioActual.puedeGestionar(autenticacion, ul.getUsuario().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso sobre los datos de otro usuario");
            }
            UsuarioLogroDTO dto = m.map(ul, UsuarioLogroDTO.class);
            if (ul.getUsuario() != null) dto.setIdUsuario(ul.getUsuario().getIdUsuario());
            if (ul.getLogro() != null) dto.setIdLogro(ul.getLogro().getIdLogro());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }
    }
    @Operation(summary = "Actualizar un logro de usuario (solo ADMIN)")
    @PutMapping("/actualiza")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> actualizar(@RequestBody UsuarioLogroDTO dto) {
        Optional<UsuarioLogro> existente = usuariologroService.listId(dto.getIdUsuarioLogro());
        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }

        // Validar que el Usuario foráneo exista y esté activo (si no existe, responde 404)
        Optional<Usuario> usuarioOpt = usuarioService.listId(dto.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario con ID " + dto.getIdUsuario() + " no existe.");
        }

        // Validar que el Logro foráneo exista y esté activo (si no existe, responde 404)
        Optional<Logro> logroOpt = logroService.listId(dto.getIdLogro());
        if (logroOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El logro con ID " + dto.getIdLogro() + " no existe.");
        }

        UsuarioLogro ah = existente.get();
        ah.setUsuario(usuarioOpt.get());
        ah.setLogro(logroOpt.get());
        usuariologroService.update(ah);
        return ResponseEntity.ok("Registro actualizado correctamente");
    }
    @Operation(summary = "Quitar un logro a un usuario (borrado lógico, solo ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        Optional<UsuarioLogro> usersAchiev = usuariologroService.listId(id);
        if (usersAchiev.isPresent()) {
            usuariologroService.delete(id);
            return ResponseEntity.ok("Registro eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Registro no encontrado");
        }
    }
    @Operation(summary = "Ranking de usuarios según la cantidad de logros obtenidos")
    @GetMapping("/ranking-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<UsuarioLogrosCountDTO>> cantidadLogrosPorUsuario() {
        List<String[]> lista = usuariologroService.cantidadLogrosPorUsuario();
        List<UsuarioLogrosCountDTO> listaDTO = new ArrayList<>();
        for (String[] columna : lista) {
            UsuarioLogrosCountDTO dto = new UsuarioLogrosCountDTO();
            dto.setNombreUsuario(columna[0]);
            dto.setTotalLogros(Long.parseLong(columna[1]));
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }

    @Operation(summary = "Listar los logros más obtenidos por los usuarios")
    @GetMapping("/logros-populares")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
    public ResponseEntity<List<LogroPopularidadDTO>> logrosMasObtenidos() {
        List<String[]> lista = usuariologroService.logrosMasObtenidos();
        List<LogroPopularidadDTO> listaDTO = new ArrayList<>();
        for (String[] columna : lista) {
            LogroPopularidadDTO dto = new LogroPopularidadDTO();
            dto.setNombreLogro(columna[0]);
            dto.setCantidadUsuarios(Long.parseLong(columna[1]));
            listaDTO.add(dto);
        }
        return ResponseEntity.ok(listaDTO);
    }
}
