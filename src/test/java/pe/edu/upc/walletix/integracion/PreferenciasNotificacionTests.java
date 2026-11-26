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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN de la US25: elegir qué notificaciones automáticas recibir
@SpringBootTest
@AutoConfigureMockMvc
class PreferenciasNotificacionTests {

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
    private int idCategoria;
    private int idComercio;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        tokenAdmin = api.login(api.registrarAdmin());
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        idComercio = api.crearComercio(token, idCategoria);
        // Logro que se desbloquea con el primer gasto (genera una notificación de tipo "logro")
        String lista = mockMvc.perform(get("/logros").header("Authorization", "Bearer " + tokenAdmin))
                .andReturn().getResponse().getContentAsString();
        if (!((List<String>) JsonPath.read(lista, "$[*].nombreLogro")).contains("Primer gasto registrado")) {
            mockMvc.perform(post("/logros/web").header("Authorization", "Bearer " + tokenAdmin).contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombreLogro\":\"Primer gasto registrado\",\"descripcionLogro\":\"Prueba\",\"urlIconoLogro\":\"x\",\"puntosLogro\":10}"))
                    .andExpect(status().isCreated());
        }
    }

    private ResultActions guardar(String json) throws Exception {
        return mockMvc.perform(put("/notificaciones/preferencias").header("Authorization", "Bearer " + token)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // Tipos de mis notificaciones
    private List<String> tipos() throws Exception {
        String json = mockMvc.perform(get("/notificaciones").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(json, "$[*].tipo");
    }

    @Test
    @DisplayName("US25: por defecto recibe todo, y guarda y devuelve las preferencias elegidas")
    void guardaLasPreferencias() throws Exception {
        mockMvc.perform(get("/notificaciones/preferencias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.alerta").value(true))
                .andExpect(jsonPath("$.logro").value(true))
                .andExpect(jsonPath("$.reto").value(true));

        guardar("{\"alerta\":false,\"logro\":true,\"reto\":false}").andExpect(status().isOk());

        mockMvc.perform(get("/notificaciones/preferencias").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.alerta").value(false))
                .andExpect(jsonPath("$.logro").value(true))
                .andExpect(jsonPath("$.reto").value(false));
    }

    @Test
    @DisplayName("US25: con las alertas desactivadas, superar el presupuesto no llega a la campana (pero el aviso al guardar sí sale)")
    void sinAlertas() throws Exception {
        guardar("{\"alerta\":false,\"logro\":false,\"reto\":true}").andExpect(status().isOk());
        api.crearPresupuesto(token, idUsuario, idCategoria, "50");
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra grande", "80", LocalDate.now())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertaPresupuesto").exists()); // US15: el aviso inmediato no es una notificación
        assertThat(tipos()).doesNotContain("alerta").doesNotContain("logro");
    }

    @Test
    @DisplayName("US25: con todo activado (por defecto) llegan la alerta de presupuesto y el logro")
    void conTodoActivado() throws Exception {
        api.crearPresupuesto(token, idUsuario, idCategoria, "50");
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra grande", "80", LocalDate.now()));
        assertThat(tipos()).contains("alerta", "logro");
    }

    @Test
    @DisplayName("US25: hay que indicar los tres tipos; sin sesión responde 401")
    void validaciones() throws Exception {
        guardar("{\"alerta\":true}").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Indica si quieres recibir")));
        guardar("").andExpect(status().isBadRequest());
        mockMvc.perform(get("/notificaciones/preferencias")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("US25: las preferencias son de cada usuario y no afectan a otro")
    void porUsuario() throws Exception {
        guardar("{\"alerta\":false,\"logro\":false,\"reto\":false}").andExpect(status().isOk());
        String otro = api.login(api.registrarUsuario());
        mockMvc.perform(get("/notificaciones/preferencias").header("Authorization", "Bearer " + otro))
                .andExpect(jsonPath("$.alerta").value(true))
                .andExpect(jsonPath("$.logro").value(true))
                .andExpect(jsonPath("$.reto").value(true));
    }
}
