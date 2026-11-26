package pe.edu.upc.walletix.integracion;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN de la US39: exportar mis datos financieros en PDF
@SpringBootTest
@AutoConfigureMockMvc
class ReportesIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;
    @MockitoBean
    private Reloj reloj;

    private ApiPruebas api;
    private String token;
    private int idUsuario;
    private final String esteMes = YearMonth.now().toString();

    @BeforeEach
    void preparar() throws Exception {
        when(reloj.hoy()).thenReturn(LocalDate.now());
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
    }

    private ResultActions reporte(String tokenUsado, String desde, String hasta, String idioma) throws Exception {
        return mockMvc.perform(get("/reportes/pdf").param("desde", desde).param("hasta", hasta)
                .header("Authorization", "Bearer " + tokenUsado).header("Accept-Language", idioma));
    }

    // Texto de todas las páginas del PDF
    private static String texto(MvcResult resultado) throws Exception {
        PdfReader lector = new PdfReader(resultado.getResponse().getContentAsByteArray());
        PdfTextExtractor extractor = new PdfTextExtractor(lector);
        StringBuilder texto = new StringBuilder();
        for (int pagina = 1; pagina <= lector.getNumberOfPages(); pagina++) {
            texto.append(extractor.getTextFromPage(pagina)).append('\n');
        }
        lector.close();
        return texto.toString();
    }

    @Test
    @DisplayName("US39 escenario 1: con movimientos, descarga un PDF con el resumen, las categorías y los movimientos")
    void exportaElPdf() throws Exception {
        int idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        int idComercio = api.crearComercio(token, idCategoria);
        api.crearIngreso(token, idUsuario, idCategoriaIngreso, "1500");
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Mercado semanal", "120.50", LocalDate.now()));
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Galletas", "3.50", LocalDate.now()));

        MvcResult resultado = reporte(token, esteMes, esteMes, "es-419")
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")))
                .andExpect(header().string("Content-Disposition", containsString("walletix-reporte-" + esteMes + ".pdf")))
                .andReturn();
        byte[] pdf = resultado.getResponse().getContentAsByteArray();
        assertThat(new String(pdf, 0, 5)).isEqualTo("%PDF-");

        String texto = texto(resultado);
        assertThat(texto).contains("Reporte financiero Walletix");
        assertThat(texto).contains("S/ 1,500.00");   // total de ingresos
        assertThat(texto).contains("S/ 124.00");     // total de gastos
        assertThat(texto).contains("S/ 1,376.00");   // diferencia
        assertThat(texto).contains("Mercado semanal").contains("Galletas");
        assertThat(texto).contains("-S/ 120.50");    // los gastos salen en negativo
        assertThat(texto).contains("S/ 3.50 en 1 compra").doesNotContain("1 compras"); // gastos hormiga, en singular
    }

    @Test
    @DisplayName("US39 escenario 2: sin movimientos muestra que no hay información para exportar")
    void sinMovimientos() throws Exception {
        reporte(token, esteMes, esteMes, "es-419")
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("no hay información para exportar")));
        reporte(token, esteMes, esteMes, "en-US")
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("there is no information to export")));
    }

    @Test
    @DisplayName("Valida el periodo: meses invertidos, futuros, demasiado largos o con mal formato")
    void periodoInvalido() throws Exception {
        String mesPasado = YearMonth.now().minusMonths(1).toString();
        String mesFuturo = YearMonth.now().plusMonths(1).toString();
        reporte(token, esteMes, mesPasado, "es-419").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("El mes inicial no puede ser posterior al mes final")));
        reporte(token, esteMes, mesFuturo, "es-419").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("mes futuro")));
        reporte(token, YearMonth.now().minusMonths(30).toString(), esteMes, "es-419").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("hasta 24 meses")));
        reporte(token, "noviembre", esteMes, "es-419").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("AAAA-MM")));
    }

    @Test
    @DisplayName("Seguridad: sin sesión responde 401 y el reporte nunca incluye movimientos de otro usuario")
    void soloMisDatos() throws Exception {
        mockMvc.perform(get("/reportes/pdf").param("desde", esteMes).param("hasta", esteMes))
                .andExpect(status().isUnauthorized());

        // Otro usuario registra un gasto
        String otroCorreo = api.registrarUsuario();
        String tokenOtro = api.login(otroCorreo);
        int idOtro = api.idDe(otroCorreo);
        int catOtro = api.crearCategoria(tokenOtro, idOtro, "gasto");
        int comOtro = api.crearComercio(tokenOtro, catOtro);
        api.crearGasto(tokenOtro, api.jsonGasto(0, idOtro, catOtro, comOtro, "Gasto privado del otro", "77", LocalDate.now()));

        // Mi reporte: sin movimientos propios -> 404, aunque el otro tenga datos
        reporte(token, esteMes, esteMes, "es-419").andExpect(status().isNotFound());

        // Con un gasto mío, el PDF no contiene el del otro
        int miCategoria = api.crearCategoria(token, idUsuario, "gasto");
        int miComercio = api.crearComercio(token, miCategoria);
        api.crearGasto(token, api.jsonGasto(0, idUsuario, miCategoria, miComercio, "Mi gasto", "10", LocalDate.now()));
        String texto = texto(reporte(token, esteMes, esteMes, "es-419").andExpect(status().isOk()).andReturn());
        assertThat(texto).contains("Mi gasto").doesNotContain("Gasto privado del otro");
    }

    @Test
    @DisplayName("El PDF sale en inglés si la petición es en inglés")
    void enIngles() throws Exception {
        int idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        int idComercio = api.crearComercio(token, idCategoria);
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Lunch", "15", LocalDate.now()));
        String texto = texto(reporte(token, esteMes, esteMes, "en-US").andExpect(status().isOk()).andReturn());
        assertThat(texto).contains("Walletix financial report").contains("Expenses by category").contains("Lunch");
        // Un solo mes: "Period: November 2026" (sin "to November 2026") y la hora con su espacio ("10:33 AM")
        assertThat(texto).doesNotContain(" to ").containsPattern("\\d:\\d{2} [AP]M");
    }
}
