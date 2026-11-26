package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.RetosDTOs.RetoPersonalDTO;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.securities.CandadoUsuario;
import pe.edu.upc.walletix.securities.UsuarioActual;
import pe.edu.upc.walletix.servicesinterfaces.IGamificacionService;
import pe.edu.upc.walletix.servicesinterfaces.IRetoService;
import pe.edu.upc.walletix.servicesinterfaces.IRetoService.ReglaRetoException;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.Optional;
import java.util.function.Function;

// Centro de Retos y Logros (US26-US30). Todo es del usuario que inició sesión: sale del token, no de la URL.
// El progreso lo calcula la API (un registro por día); el cliente nunca envía porcentajes ni estados.
@Tag(name = "Centro de Retos y Logros", description = "Retos con progreso diario, nivel, racha y vitrina de medallas")
@RestController
@RequestMapping("/retos")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
public class RetoController {
    @Autowired
    private IRetoService retoService;
    @Autowired
    private IGamificacionService gamificacionService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private CandadoUsuario candado;
    @Autowired
    private Mensajes mensajes;

    // Busca al usuario del token y ejecuta la acción; las reglas incumplidas responden con su código (400, 403, 404, 409)
    // Acciones que cambian datos o dan puntos: van con el candado del usuario. El usuario se carga DENTRO del
    // candado, así sus puntos están al día aunque llegue otra petición suya a la vez (ej. doble clic)
    private ResponseEntity<?> conUsuario(Authentication autenticacion, Function<Usuario, Object> accion, HttpStatus exito) {
        return candado.ejecutar(usuarioActual.id(autenticacion), () -> consulta(autenticacion, accion, exito));
    }

    // Consultas: sin candado, para que la página pueda pedir sus datos en paralelo
    private ResponseEntity<?> consulta(Authentication autenticacion, Function<Usuario, Object> accion, HttpStatus exito) {
        Optional<Usuario> usuario = usuarioService.listId(usuarioActual.id(autenticacion));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        }
        try {
            return ResponseEntity.status(exito).body(accion.apply(usuario.get()));
        } catch (ReglaRetoException error) {
            return ResponseEntity.status(error.getStatus()).body(error.getMessage());
        }
    }

    @Operation(summary = "Resumen: nivel, XP, efectividad, dinero blindado, racha y mis retos (US26)")
    @GetMapping("/resumen")
    public ResponseEntity<?> resumen(Authentication autenticacion) {
        return consulta(autenticacion, usuario -> {
            gamificacionService.revisarLogros(usuario); // por si ya cumple algún logro
            return retoService.resumen(usuario);
        }, HttpStatus.OK);
    }

    @Operation(summary = "Retos oficiales disponibles y los requisitos que me faltan (US27, US28)")
    @GetMapping("/disponibles")
    public ResponseEntity<?> disponibles(Authentication autenticacion) {
        return consulta(autenticacion, retoService::disponibles, HttpStatus.OK);
    }

    @Operation(summary = "Unirme a un reto oficial; responde 400 con los requisitos que faltan (US27, US28)")
    @PostMapping("/{idDesafio}/unirme")
    public ResponseEntity<?> unirme(@PathVariable int idDesafio, Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> retoService.unirse(usuario, idDesafio), HttpStatus.CREATED);
    }

    @Operation(summary = "Registrar mi progreso de hoy en un reto (+20 XP, una vez por día)")
    @PostMapping("/participaciones/{idUsuarioDesafio}/progreso")
    public ResponseEntity<?> registrarProgreso(@PathVariable int idUsuarioDesafio, Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> retoService.registrarProgreso(usuario, idUsuarioDesafio), HttpStatus.OK);
    }

    @Operation(summary = "Abandonar un reto en progreso")
    @PutMapping("/participaciones/{idUsuarioDesafio}/abandonar")
    public ResponseEntity<?> abandonar(@PathVariable int idUsuarioDesafio, Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> retoService.abandonar(usuario, idUsuarioDesafio), HttpStatus.OK);
    }

    @Operation(summary = "Crear un reto personal: tipo, duración y meta de ahorro (US29)")
    @PostMapping("/personal")
    public ResponseEntity<?> crearPersonal(@RequestBody RetoPersonalDTO datos, Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> retoService.crearPersonal(usuario, datos), HttpStatus.CREATED);
    }

    // Solo lectura: los logros nuevos se revisan en /resumen (así dos peticiones no los revisan a la vez)
    @Operation(summary = "Vitrina de medallas: todos los logros, si los tengo y su avance (US30)")
    @GetMapping("/medallas")
    public ResponseEntity<?> medallas(Authentication autenticacion) {
        return consulta(autenticacion, gamificacionService::medallas, HttpStatus.OK);
    }
}
