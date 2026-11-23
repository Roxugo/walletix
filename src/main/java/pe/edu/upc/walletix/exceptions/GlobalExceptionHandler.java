package pe.edu.upc.walletix.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.edu.upc.walletix.i18n.Mensajes;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Atrapa los errores de toda la API y responde un mensaje claro en vez de un error 500 con todo el detalle
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Mensajes en el idioma de la petición (Accept-Language): es-419 o en-US
    @Autowired
    private Mensajes mensajes;

    private ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", mensaje);
        return new ResponseEntity<>(respuesta, estado);
    }

    // JSON mal escrito o con un tipo de dato incorrecto (ej. texto en un campo numérico o fecha inválida)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarJsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, mensajes.get("cuerpo.peticion.no.valido.revise"));
    }

    // Parámetro de la URL con un tipo incorrecto (ej. /gastos/abc en vez de /gastos/1)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarParametroInvalido(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, mensajes.get("parametro.valor.invalido", e.getName(), String.valueOf(e.getValue())));
    }

    // Falta un parámetro obligatorio (ej. ?fechaInicio=...)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> manejarParametroFaltante(MissingServletRequestParameterException e) {
        return responder(HttpStatus.BAD_REQUEST, mensajes.get("parametro.obligatorio.falta", e.getParameterName()));
    }

    // Se rompe una regla de la base de datos. Según el código de PostgreSQL se da un mensaje específico
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarIntegridad(DataIntegrityViolationException e) {
        Throwable causa = e.getMostSpecificCause();
        String codigo = causa instanceof SQLException ? ((SQLException) causa).getSQLState() : "";
        if ("22001".equals(codigo)) {
            // Texto más largo que la columna, ej. "character varying(50)"
            Matcher largo = Pattern.compile("character varying\\((\\d+)\\)").matcher(String.valueOf(causa.getMessage()));
            String mensaje = largo.find()
                    ? mensajes.get("texto.supera.maximo", largo.group(1))
                    : mensajes.get("texto.supera.maximo.sin.limite");
            return responder(HttpStatus.BAD_REQUEST, mensaje);
        }
        if ("23505".equals(codigo)) {
            return responder(HttpStatus.CONFLICT, mensajes.get("existe.registro.ese.dato.no"));
        }
        if ("23502".equals(codigo)) {
            return responder(HttpStatus.BAD_REQUEST, mensajes.get("falta.campo.obligatorio"));
        }
        return responder(HttpStatus.CONFLICT, mensajes.get("registro.no.cumple.reglas.base"));
    }

    // El usuario tiene token, pero su rol no le permite esta acción
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(Exception e) {
        return responder(HttpStatus.FORBIDDEN, mensajes.get("no.tiene.permiso.realizar.esta"));
    }

    // Ruta que no existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRutaInexistente(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, mensajes.get("ruta.solicitada.no.existe"));
    }

    // Método HTTP equivocado (ej. GET en una ruta que solo acepta POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, mensajes.get("metodo.no.permitido", e.getMethod()));
    }

    // Cualquier otro error inesperado (como en el proyecto del profe).
    // Seguridad: el detalle (SQL, nombres de tablas) solo va al log del servidor, nunca al cliente
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(Exception e) {
        log.error("Error no controlado", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, mensajes.get("ocurrio.error.interno.intentelo.nuevamente"));
    }
}
