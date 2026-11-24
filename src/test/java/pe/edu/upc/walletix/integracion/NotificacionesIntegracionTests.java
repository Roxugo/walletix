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
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de INTEGRACIÓN del panel de notificaciones:
// US22 (ver todas, de la más reciente a la más antigua), US23 (no leídas / leídas) y US24 (marcar leída / no leída)
@SpringBootTest
@AutoConfigureMockMvc
class NotificacionesIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    private ApiPruebas api;
    private String token;
    private int idUsuario;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
    }

    private int crearNotificacion(String tokenUsado, int dueno, String titulo) throws Exception {
        String respuesta = mockMvc.perform(post("/notificaciones/web").header("Authorization", "Bearer " + tokenUsado)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":" + dueno + ",\"titulo\":\"" + titulo + "\",\"tipo\":\"aviso\",\"mensaje\":\"Mensaje de prueba\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.idNotificacion");
    }

    private String misNotificaciones(String tokenUsado) throws Exception {
        return mockMvc.perform(get("/notificaciones").header("Authorization", "Bearer " + tokenUsado))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private int noLeidas(String tokenUsado, int dueno) throws Exception {
        String respuesta = mockMvc.perform(get("/notificaciones/no-leidas/" + dueno + "/cantidad")
                        .header("Authorization", "Bearer " + tokenUsado))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return Integer.parseInt(respuesta);
    }

    @Test
    @DisplayName("US15 + US22: pasarse del presupuesto deja una notificación no leída, con fecha, en el panel")
    void alertaDePresupuestoLlegaAlPanel() throws Exception {
        int idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        int idComercio = api.crearComercio(api.login(api.registrarAdmin()), idCategoria);
        api.crearPresupuesto(token, idUsuario, idCategoria, "100");
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra grande", "150", LocalDate.now()));

        String lista = misNotificaciones(token);
        assertThat((List<String>) JsonPath.read(lista, "$[*].tipo")).containsExactly("alerta");
        assertThat((Boolean) JsonPath.read(lista, "$[0].estadoNotificacion")).isFalse();
        assertThat((String) JsonPath.read(lista, "$[0].fechaNotificacion")).startsWith(LocalDate.now().toString());
        assertThat(noLeidas(token, idUsuario)).isEqualTo(1);
    }

    @Test
    @DisplayName("US22: el panel muestra primero la notificación más reciente")
    void ordenMasRecientePrimero() throws Exception {
        crearNotificacion(token, idUsuario, "Primera");
        crearNotificacion(token, idUsuario, "Segunda");
        assertThat((List<String>) JsonPath.read(misNotificaciones(token), "$[*].titulo")).containsExactly("Segunda", "Primera");
    }

    @Test
    @DisplayName("US24: marcar una notificación como leída y volver a marcarla como no leída")
    void marcarLeidaYNoLeida() throws Exception {
        int id = crearNotificacion(token, idUsuario, "Aviso");

        mockMvc.perform(put("/notificaciones/" + id + "/leida").param("leida", "true").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoNotificacion").value(true));
        assertThat(noLeidas(token, idUsuario)).isZero();

        mockMvc.perform(put("/notificaciones/" + id + "/leida").param("leida", "false").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoNotificacion").value(false));
        assertThat(noLeidas(token, idUsuario)).isEqualTo(1);
    }

    @Test
    @DisplayName("Un usuario no puede marcar ni ver las notificaciones de otro: 403 y no aparecen en su panel")
    void notificacionesAjenas() throws Exception {
        int id = crearNotificacion(token, idUsuario, "Privada");
        String tokenOtro = api.login(api.registrarUsuario());

        mockMvc.perform(put("/notificaciones/" + id + "/leida").param("leida", "true").header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
        assertThat((List<String>) JsonPath.read(misNotificaciones(tokenOtro), "$[*].titulo")).doesNotContain("Privada");
        assertThat(noLeidas(token, idUsuario)).isEqualTo(1);
    }

    @Test
    @DisplayName("US24: \"Marcar todas como leídas\" solo cambia las del usuario que inició sesión")
    void marcarTodasSoloLasMias() throws Exception {
        crearNotificacion(token, idUsuario, "Una");
        crearNotificacion(token, idUsuario, "Dos");
        String correoOtro = api.registrarUsuario();
        String tokenOtro = api.login(correoOtro);
        int idOtro = api.idDe(correoOtro);
        crearNotificacion(tokenOtro, idOtro, "Del otro");

        mockMvc.perform(put("/notificaciones/leer-todas").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marcadas").value(2));
        assertThat(noLeidas(token, idUsuario)).isZero();
        assertThat(noLeidas(tokenOtro, idOtro)).isEqualTo(1);
    }
}
