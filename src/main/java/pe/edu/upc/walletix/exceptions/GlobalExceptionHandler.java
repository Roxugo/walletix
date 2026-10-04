package pe.edu.upc.walletix.exceptions;

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

import java.util.HashMap;
import java.util.Map;

// Atrapa los errores de toda la API y responde un mensaje claro en vez de un error 500 con todo el detalle
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", mensaje);
        return new ResponseEntity<>(respuesta, estado);
    }

    // JSON mal escrito o con un tipo de dato incorrecto (ej. texto en un campo numérico o fecha inválida)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarJsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido: revise el JSON y los tipos de datos");
    }

    // Parámetro de la URL con un tipo incorrecto (ej. /gastos/abc en vez de /gastos/1)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarParametroInvalido(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro '" + e.getName() + "' tiene un valor inválido: " + e.getValue());
    }

    // Falta un parámetro obligatorio (ej. ?fechaInicio=...)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> manejarParametroFaltante(MissingServletRequestParameterException e) {
        return responder(HttpStatus.BAD_REQUEST, "Falta el parámetro obligatorio '" + e.getParameterName() + "'");
    }

    // Se rompe una regla de la base de datos (ej. correo repetido o un campo obligatorio vacío)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarIntegridad(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT, "El registro no cumple las reglas de la base de datos (dato repetido o campo obligatorio vacío)");
    }

    // El usuario tiene token, pero su rol no le permite esta acción
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, String>> manejarAccesoDenegado(Exception e) {
        return responder(HttpStatus.FORBIDDEN, "No tiene permiso para realizar esta acción");
    }

    // Ruta que no existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRutaInexistente(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe");
    }

    // Método HTTP equivocado (ej. GET en una ruta que solo acepta POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException e) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método " + e.getMethod() + " no permitido en esta ruta");
    }

    // Cualquier otro error inesperado (como en el proyecto del profe)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorGeneral(Exception e) {
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno: " + e.getMessage());
    }
}
