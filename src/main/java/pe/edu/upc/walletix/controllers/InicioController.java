package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// Rutas públicas para saber si la API está viva (útil en la demo y para el monitoreo de Render).
// No devuelven ningún dato de usuarios, por eso no piden token.
@Tag(name = "Inicio", description = "Estado de la API")
@RestController
public class InicioController {

    @Operation(summary = "Bienvenida: nombre del servicio, estado y enlace a la documentación")
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> inicio() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("servicio", "Walletix API");
        respuesta.put("estado", "ok");
        respuesta.put("documentacion", "/swagger-ui/index.html");
        respuesta.put("login", "POST /login");
        respuesta.put("hora", Instant.now().toString());
        return ResponseEntity.ok(respuesta);
    }

    @Operation(summary = "Health check: responde ok si la API está funcionando")
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> salud() {
        return ResponseEntity.ok(Map.of("estado", "ok"));
    }
}
