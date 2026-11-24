package pe.edu.upc.walletix.aceptacion;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Definición de los pasos de los escenarios Gherkin (src/test/resources/features).
// Cada frase Given / When / Then del .feature se ejecuta con el método que tiene esa misma frase.
// Las peticiones se envían en inglés (Accept-Language: en-US), por eso los mensajes esperados están en inglés.
public class PasosDefinicion {

    private static final String IDIOMA = "en-US";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    // Estado del escenario actual (Cucumber crea un objeto nuevo por escenario)
    private ApiPruebas api;
    private ResultActions respuesta;
    private String correo;
    private String jsonRegistro;
    private String token;
    private int idUsuario;
    private int idCategoria;
    private int idComercio;
    private int idGasto;

    @Before
    public void preparar() {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
    }

    // Agrega el idioma (y el token, si hay sesión) a cada petición
    private ResultActions enviar(MockHttpServletRequestBuilder peticion) throws Exception {
        peticion.header("Accept-Language", IDIOMA);
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(peticion);
    }

    private String jsonGasto(int id, String descripcion, double monto, LocalDate fecha) {
        return api.jsonGasto(id, idUsuario, idCategoria, idComercio, descripcion, String.valueOf(monto), fecha);
    }

    // ---------- US01, US02, US03: registro e inicio de sesión ----------

    @Given("a visitor with a valid email, password and required data")
    public void unVisitanteConDatosValidos() {
        int numero = api.siguiente();
        correo = "visitante" + numero + "@walletix.test";
        jsonRegistro = api.jsonUsuario(correo, numero);
    }

    @When("the visitor submits the registration form")
    public void elVisitanteEnviaElRegistro() throws Exception {
        respuesta = enviar(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(jsonRegistro));
    }

    @Then("the account is created with status {int}")
    public void laCuentaSeCrea(int codigo) throws Exception {
        respuesta.andExpect(status().is(codigo));
        assertThat(usuarioService.buscarPorCorreo(correo)).isPresent();
    }

    @Then("the response does not include the password")
    public void laRespuestaNoIncluyeLaContrasena() throws Exception {
        respuesta.andExpect(jsonPath("$.contrasenaUsuario").doesNotExist());
    }

    @Given("a user that is already registered")
    public void unUsuarioYaRegistrado() throws Exception {
        correo = api.registrarUsuario();
    }

    @When("another visitor tries to register with the same email")
    public void otroVisitanteUsaElMismoCorreo() throws Exception {
        String mismoCorreo = api.jsonUsuario(correo, api.siguiente()); // otro teléfono, mismo correo
        respuesta = enviar(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(mismoCorreo));
    }

    @When("the user logs in with the correct password")
    public void iniciaSesionConLaContrasenaCorrecta() throws Exception {
        iniciaSesionCon(ApiPruebas.CONTRASENA);
    }

    @When("the user logs in with the password {string}")
    public void iniciaSesionCon(String contrasena) throws Exception {
        respuesta = enviar(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}"));
    }

    @Then("the system returns a JWT token")
    public void devuelveUnToken() throws Exception {
        String cuerpo = respuesta.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        token = JsonPath.read(cuerpo, "$.token");
        assertThat(token.split("\\.")).hasSize(3); // cabecera.payload.firma
    }

    @Then("the token gives access to the user's own profile")
    public void elTokenDaAccesoAlPerfil() throws Exception {
        enviar(get("/usuarios/yo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoUsuario").value(correo));
    }

    // ---------- Pasos comunes ----------

    @Then("the request is rejected with status {int}")
    public void seRechazaConEstado(int codigo) throws Exception {
        respuesta.andExpect(status().is(codigo));
    }

    @Then("the message {string} is shown")
    public void seMuestraElMensaje(String mensaje) throws Exception {
        // Los controllers responden texto plano y el GlobalExceptionHandler {"error": "..."}: basta con que lo contenga
        String cuerpo = respuesta.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(cuerpo).contains(mensaje);
    }

    // ---------- US11, US13, US14, US16, US19: gastos ----------

    @Given("a logged-in user with an expense category and a merchant")
    public void unUsuarioConCategoriaYComercio() throws Exception {
        correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        idComercio = api.crearComercio(api.login(api.registrarAdmin()), idCategoria);
    }

    @When("the user registers an expense of {double} described as {string}")
    public void registraUnGasto(double monto, String descripcion) throws Exception {
        respuesta = enviar(post("/gastos/web").contentType(MediaType.APPLICATION_JSON)
                .content(jsonGasto(0, descripcion, monto, LocalDate.now())));
    }

    @Then("the expense is saved with status {int}")
    public void elGastoSeGuarda(int codigo) throws Exception {
        respuesta.andExpect(status().is(codigo)).andExpect(jsonPath("$.idGasto").isNumber());
    }

    @Then("the expense list of the user contains {string}")
    public void laListaContiene(String descripcion) throws Exception {
        enviar(get("/gastos")).andExpect(jsonPath("$[*].descripcionGasto", hasItem(descripcion)));
    }

    @Given("the user has an expense described as {string} dated today")
    public void tieneUnGastoDeHoy(String descripcion) throws Exception {
        idGasto = api.crearGasto(token, jsonGasto(0, descripcion, 15, LocalDate.now()));
    }

    @Given("the user has an expense described as {string} dated {int} months ago")
    public void tieneUnGastoDeHaceMeses(String descripcion, int meses) throws Exception {
        idGasto = api.crearGasto(token, jsonGasto(0, descripcion, 20, LocalDate.now().minusMonths(meses)));
    }

    @When("the user changes the description to {string} and the amount to {double}")
    public void editaElGasto(String descripcion, double monto) throws Exception {
        respuesta = enviar(put("/gastos/actualiza").contentType(MediaType.APPLICATION_JSON)
                .content(jsonGasto(idGasto, descripcion, monto, LocalDate.now())));
        respuesta.andExpect(status().isOk());
    }

    @Then("the expense list shows {string} with an amount of {double}")
    public void laListaMuestraElGastoEditado(String descripcion, double monto) throws Exception {
        enviar(get("/gastos"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcionGasto").value(descripcion))
                .andExpect(jsonPath("$[0].montoGasto").value(monto));
    }

    @When("the user confirms the deletion of the expense")
    public void eliminaElGasto() throws Exception {
        respuesta = enviar(delete("/gastos/" + idGasto));
        respuesta.andExpect(status().isOk());
    }

    @Then("the expense no longer appears in the list of the user")
    public void elGastoYaNoAparece() throws Exception {
        enviar(get("/gastos")).andExpect(jsonPath("$", hasSize(0)));
    }

    @When("the user filters the expenses from the first day of this month until today")
    public void filtraElMesActual() throws Exception {
        LocalDate hoy = LocalDate.now();
        filtraEntre(hoy.withDayOfMonth(1).toString(), hoy.toString());
    }

    @When("the user filters the expenses from {string} until {string}")
    public void filtraEntre(String inicio, String fin) throws Exception {
        respuesta = enviar(get("/gastos/usuario/" + idUsuario).param("fechaInicio", inicio).param("fechaFin", fin));
    }

    // ---------- US11, US12, US14: saldo / US15: alerta de presupuesto ----------

    @Then("the balance of the user is {double}")
    public void elSaldoEs(double saldoEsperado) throws Exception {
        assertThat(api.saldo(token)).isEqualByComparingTo(java.math.BigDecimal.valueOf(saldoEsperado));
    }

    @When("the user registers an income of {double}")
    public void registraUnIngreso(double monto) throws Exception {
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        api.crearIngreso(token, idUsuario, idCategoriaIngreso, String.valueOf(monto));
    }

    @Given("the user has a budget of {double} for the expense category this month")
    public void tieneUnPresupuesto(double monto) throws Exception {
        api.crearPresupuesto(token, idUsuario, idCategoria, String.valueOf(monto));
    }

    @Given("the user has registered an expense of {double}")
    public void yaRegistroUnGasto(double monto) throws Exception {
        api.crearGasto(token, jsonGasto(0, "Previous expense", monto, LocalDate.now()));
    }

    @Then("the response includes the budget alert {string}")
    public void incluyeLaAlerta(String texto) throws Exception {
        respuesta.andExpect(jsonPath("$.alertaPresupuesto").value(org.hamcrest.Matchers.containsString(texto)));
    }

    @Then("the response does not include a budget alert")
    public void noIncluyeAlerta() throws Exception {
        respuesta.andExpect(jsonPath("$.alertaPresupuesto").doesNotExist());
    }

    @Then("only the expense {string} is shown")
    public void soloSeMuestra(String descripcion) throws Exception {
        respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcionGasto").value(descripcion));
    }
}
