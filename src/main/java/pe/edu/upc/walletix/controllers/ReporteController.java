package pe.edu.upc.walletix.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.i18n.Mensajes;
import pe.edu.upc.walletix.securities.UsuarioActual;
import pe.edu.upc.walletix.servicesinterfaces.IReporteService;
import pe.edu.upc.walletix.servicesinterfaces.IReporteService.ReglaReporteException;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Optional;

// US39: exportar mis datos financieros en PDF. El reporte es del usuario del token (un ADMIN también recibe solo el suyo).
@Tag(name = "Reportes", description = "Reporte financiero en PDF (US39)")
@RestController
@RequestMapping("/reportes")
@PreAuthorize("hasAnyAuthority('ADMIN', 'USUARIO')")
public class ReporteController {
    @Autowired
    private IReporteService reporteService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private UsuarioActual usuarioActual;
    @Autowired
    private Mensajes mensajes;

    @Operation(summary = "Descargar mi reporte financiero en PDF. Ej: /reportes/pdf?desde=2026-09&hasta=2026-11")
    @GetMapping("/pdf")
    public ResponseEntity<?> pdf(@RequestParam String desde, @RequestParam String hasta, Authentication autenticacion) {
        YearMonth inicio;
        YearMonth fin;
        try {
            inicio = YearMonth.parse(desde);
            fin = YearMonth.parse(hasta);
        } catch (DateTimeParseException error) {
            return ResponseEntity.badRequest().body(mensajes.get("reporte.mes.invalido"));
        }
        Optional<Usuario> usuario = usuarioService.listId(usuarioActual.id(autenticacion));
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensajes.get("usuario.no.encontrado"));
        }
        try {
            byte[] pdf = reporteService.reportePdf(usuario.get(), inicio, fin);
            String nombre = "walletix-reporte-" + inicio + (inicio.equals(fin) ? "" : "_" + fin) + ".pdf";
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(nombre).build().toString())
                    .body(pdf);
        } catch (ReglaReporteException error) {
            // Con charset: si no, el cliente lee "información" como "informaciÃ³n"
            return ResponseEntity.status(error.getStatus()).contentType(new MediaType(MediaType.TEXT_PLAIN, java.nio.charset.StandardCharsets.UTF_8))
                    .body(error.getMessage());
        }
    }
}
