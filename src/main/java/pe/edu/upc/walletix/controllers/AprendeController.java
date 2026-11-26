package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.walletix.dtos.AprendeDTOs.RespuestasQuizDTO;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.securities.CandadoUsuario;
import pe.edu.upc.walletix.securities.UsuarioActual;
import pe.edu.upc.walletix.servicesinterfaces.IAprendeService;
import pe.edu.upc.walletix.servicesinterfaces.IAprendeService.ReglaAprendeException;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.Optional;
import java.util.function.Function;

// Sección "Aprende" (US31-US35). Todo es del usuario que inició sesión: sale del token, no de la URL.
// El quiz lo califica la API: el navegador nunca recibe la respuesta correcta antes de enviar.
@Tag(name = "Aprende", description = "Consejos por categoría, favoritos, contenido bloqueado, microlecciones y quiz")
@RestController
@RequestMapping("/aprende")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
public class AprendeController {
    @Autowired
    private IAprendeService aprendeService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private CandadoUsuario candado;
    @Autowired
    private Mensajes mensajes;

    // Busca al usuario del token y ejecuta la acción; las reglas incumplidas responden con su código (400, 403, 404)
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
        } catch (ReglaAprendeException error) {
            return ResponseEntity.status(error.getStatus()).body(error.getMessage());
        }
    }

    @Operation(summary = "Consejos y microlecciones; filtra por categoría, texto o favoritos (US31, US32, US33)")
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) String categoria,
                                    @RequestParam(required = false) String buscar,
                                    @RequestParam(defaultValue = "false") boolean favoritos,
                                    Authentication autenticacion) {
        return consulta(autenticacion, usuario -> aprendeService.listar(usuario, categoria, buscar, favoritos), HttpStatus.OK);
    }

    @Operation(summary = "Tip de 30 segundos del día (204 si no hay consejos)")
    @GetMapping("/tip-del-dia")
    public ResponseEntity<?> tipDelDia(Authentication autenticacion) {
        ResponseEntity<?> respuesta = consulta(autenticacion, aprendeService::tipDelDia, HttpStatus.OK);
        return respuesta.getStatusCode().is2xxSuccessful() && respuesta.getBody() == null
                ? ResponseEntity.noContent().build() : respuesta;
    }

    @Operation(summary = "Abrir una microlección o consejo: contenido y preguntas sin la respuesta (US34); 403 si está bloqueado (US33)")
    @GetMapping("/{idMicroleccion}")
    public ResponseEntity<?> abrir(@PathVariable int idMicroleccion, Authentication autenticacion) {
        return consulta(autenticacion, usuario -> aprendeService.abrir(usuario, idMicroleccion), HttpStatus.OK);
    }

    @Operation(summary = "Enviar las respuestas del quiz: puntaje, puntos y retroalimentación (US35)")
    @PostMapping("/{idMicroleccion}/quiz")
    public ResponseEntity<?> responderQuiz(@PathVariable int idMicroleccion, @RequestBody(required = false) RespuestasQuizDTO respuestas,
                                           Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> aprendeService.responderQuiz(usuario, idMicroleccion, respuestas), HttpStatus.OK);
    }

    @Operation(summary = "Marcar o quitar de favoritos (US32)")
    @PutMapping("/{idMicroleccion}/favorito")
    public ResponseEntity<?> favorito(@PathVariable int idMicroleccion, Authentication autenticacion) {
        return conUsuario(autenticacion, usuario -> aprendeService.alternarFavorito(usuario, idMicroleccion), HttpStatus.OK);
    }
}
