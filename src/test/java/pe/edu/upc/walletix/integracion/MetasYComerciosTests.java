package pe.edu.upc.walletix.integracion;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN de los detalles encontrados en la prueba manual de todo el flujo:
// metas de ahorro coherentes con su monto y comercios de la categoría del gasto
@SpringBootTest
@AutoConfigureMockMvc
class MetasYComerciosTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    private ApiPruebas api;
    private String token;
    private String tokenAdmin;
    private int idUsuario;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        tokenAdmin = api.login(api.registrarAdmin());
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
    }

    private String jsonMeta(int idMeta, String objetivo, String ahorrado, String estado) {
        return """
                {"idMetaAhorro":%d,"idUsuario":%d,"titulo":"Viaje a Cusco","montoObjetivo":%s,"montoActual":%s,
                 "fechaLimite":"%s","estadoMetaAhorro":"%s"}
                """.formatted(idMeta, idUsuario, objetivo, ahorrado, LocalDate.now().plusMonths(4), estado);
    }

    private ResultActions crearMeta(String json) throws Exception {
        return mockMvc.perform(post("/metas-ahorro/web").header("Authorization", "Bearer " + token)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions editarMeta(String json) throws Exception {
        return mockMvc.perform(put("/metas-ahorro/actualiza").header("Authorization", "Bearer " + token)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private int nuevaMeta() throws Exception {
        String respuesta = crearMeta(jsonMeta(0, "1000", "200", "activa")).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.idMetaAhorro");
    }

    @Test
    @DisplayName("Metas: una meta nueva no puede nacer cumplida ni con más ahorrado que el objetivo")
    void metaNuevaYaCumplida() throws Exception {
        crearMeta(jsonMeta(0, "1000", "1200", "activa")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("no puede superar el monto objetivo")));
        crearMeta(jsonMeta(0, "1000", "1000", "activa")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Una meta nueva debe tener algo por ahorrar")));
        crearMeta(jsonMeta(0, "1000", "200", "activa")).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Metas: al editar, lo ahorrado no supera el objetivo y 'cumplida' exige llegar al objetivo")
    void editarMetaCoherente() throws Exception {
        int idMeta = nuevaMeta();
        editarMeta(jsonMeta(idMeta, "1000", "1200", "activa")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("no puede superar el monto objetivo")));
        editarMeta(jsonMeta(idMeta, "1000", "300", "cumplida")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("debe llegar al objetivo")));
        editarMeta(jsonMeta(idMeta, "1000", "300", "terminada")).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("activa, cumplida o pausada")));
    }

    @Test
    @DisplayName("Metas: al llegar al objetivo pasa sola a 'cumplida'; los estados antiguos se leen como activa / cumplida")
    void llegarAlObjetivoLaCumple() throws Exception {
        int idMeta = nuevaMeta();
        editarMeta(jsonMeta(idMeta, "1000", "1000", "activa")).andExpect(status().isOk());
        mockMvc.perform(get("/metas-ahorro/" + idMeta).header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.estadoMetaAhorro").value("cumplida"));

        // "EN_PROGRESO" (como las metas antiguas de la base) se acepta y se devuelve como "activa"
        editarMeta(jsonMeta(idMeta, "1000", "500", "EN_PROGRESO")).andExpect(status().isOk());
        mockMvc.perform(get("/metas-ahorro/" + idMeta).header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.estadoMetaAhorro").value("activa"));
    }

    @Test
    @DisplayName("Gastos: el comercio debe ser de la categoría del gasto (al crear y al editar)")
    void comercioDeLaCategoria() throws Exception {
        int comida = api.crearCategoria(token, idUsuario, "gasto");
        int salud = api.crearCategoria(token, idUsuario, "gasto");
        int bodega = api.crearComercio(tokenAdmin, comida);
        int farmacia = api.crearComercio(tokenAdmin, salud);

        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(0, idUsuario, salud, bodega, "Pastillas", "20", LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("El comercio no es de esa categoría")));

        int idGasto = api.crearGasto(token, api.jsonGasto(0, idUsuario, salud, farmacia, "Pastillas", "20", LocalDate.now()));
        mockMvc.perform(put("/gastos/actualiza").header("Authorization", "Bearer " + token).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(idGasto, idUsuario, comida, farmacia, "Pastillas", "20", LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("El comercio no es de esa categoría")));
    }
}
