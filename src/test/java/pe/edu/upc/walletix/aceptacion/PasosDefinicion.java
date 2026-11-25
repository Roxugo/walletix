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

    @When("the user creates another budget of {double} for the same category this month")
    public void creaOtroPresupuesto(double monto) throws Exception {
        LocalDate hoy = LocalDate.now();
        String json = """
                {"mes":%d,"anio":%d,"montoAsignado":%s,"idUsuario":%d,"idCategoria":%d}
                """.formatted(hoy.getMonthValue(), hoy.getYear(), monto, idUsuario, idCategoria);
        respuesta = enviar(post("/presupuestos/web").contentType(MediaType.APPLICATION_JSON).content(json));
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

    // ---------- US26, US27, US28, US29: Centro de Retos y Logros ----------

    private final java.util.Map<String, Integer> retos = new java.util.HashMap<>();
    private int idParticipacion;

    // Reto oficial creado por un ADMIN: empieza hoy y dura un mes
    private void crearRetoOficial(String titulo, int dias, int edadMinima) throws Exception {
        String tokenAdmin = api.login(api.registrarAdmin());
        LocalDate hoy = LocalDate.now();
        String json = """
                {"titulo":"%s","descripcion":"Reto de prueba","montoObjetivo":70,"puntosRecompensa":50,
                 "fechaInicio":"%s","fechaFin":"%s","saldoInicial":0,"edadMinima":%d,"saldoProyectado":70,"duracionDias":%d}
                """.formatted(titulo, hoy, hoy.plusDays(30), edadMinima, dias);
        String creado = mockMvc.perform(post("/desafios/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        retos.put(titulo, JsonPath.read(creado, "$.idDesafio"));
    }

    @Given("an official challenge {string} of {int} days that started today")
    public void unRetoOficial(String titulo, int dias) throws Exception {
        crearRetoOficial(titulo, dias, 18);
    }

    @Given("an official challenge {string} only for users of at least {int} years")
    public void unRetoConEdadMinima(String titulo, int edad) throws Exception {
        crearRetoOficial(titulo, 7, edad);
    }

    @When("the user joins the challenge {string}")
    public void seUneAlReto(String titulo) throws Exception {
        respuesta = enviar(post("/retos/" + retos.get(titulo) + "/unirme"));
        String cuerpo = respuesta.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        if (respuesta.andReturn().getResponse().getStatus() == 201) {
            idParticipacion = JsonPath.read(cuerpo, "$.idUsuarioDesafio");
        }
    }

    @Given("the user has joined the challenge {string}")
    public void yaSeUnio(String titulo) throws Exception {
        seUneAlReto(titulo);
        respuesta.andExpect(status().isCreated());
    }

    @Then("the user is added to the challenge with status {int}")
    public void quedaInscrito(int codigo) throws Exception {
        respuesta.andExpect(status().is(codigo)).andExpect(jsonPath("$.estadoDesafio").value("EN_PROGRESO"));
    }

    @Then("the challenge {string} appears in the active challenges on day {int} of {int}")
    public void apareceEnActivos(String titulo, int dia, int duracion) throws Exception {
        enviar(get("/retos/resumen")).andExpect(status().isOk())
                .andExpect(jsonPath("$.activos[0].titulo").value(titulo))
                .andExpect(jsonPath("$.activos[0].diaActual").value(dia))
                .andExpect(jsonPath("$.activos[0].duracionDias").value(duracion));
    }

    @When("the user registers today's progress in the challenge")
    public void registraElProgreso() throws Exception {
        respuesta = enviar(post("/retos/participaciones/" + idParticipacion + "/progreso"));
    }

    @Then("the user earns {int} XP and is on day {int} of {int}")
    public void ganaXp(int xp, int dia, int duracion) throws Exception {
        respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$.xpGanado").value(xp))
                .andExpect(jsonPath("$.diaActual").value(dia))
                .andExpect(jsonPath("$.duracionDias").value(duracion));
    }

    @When("the user creates a personal challenge of type {string} for {int} days to save {double}")
    public void creaRetoPersonal(String tipo, int dias, double meta) throws Exception {
        respuesta = enviar(post("/retos/personal").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipo\":\"" + tipo + "\",\"duracionDias\":" + dias + ",\"montoObjetivo\":" + meta + "}"));
    }

    @Then("the personal challenge {string} is created, is not official and gives {int} reward points")
    public void retoPersonalCreado(String titulo, int puntos) throws Exception {
        respuesta.andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value(titulo))
                .andExpect(jsonPath("$.oficial").value(false))
                .andExpect(jsonPath("$.puntosRecompensa").value(puntos))
                .andExpect(jsonPath("$.estadoDesafio").value("EN_PROGRESO"));
    }

    // ---------- US31, US32, US33, US34, US35: Aprende (consejos, microlecciones y quiz) ----------

    private final java.util.Map<String, Integer> contenidos = new java.util.HashMap<>();
    private final java.util.Map<String, java.util.List<Integer>> preguntas = new java.util.HashMap<>();

    // Contenido creado por un ADMIN; cada pregunta tiene la "a" como respuesta correcta
    private void crearContenido(String titulo, String tipo, String categoria, boolean bloqueado, int numeroPreguntas) throws Exception {
        String tokenAdmin = api.login(api.registrarAdmin());
        String json = """
                {"tituloMicroleccion":"%s","descripcionMicroleccion":"Test","contenidoMicroleccion":"Save a little every day.",
                 "urlMediaMicroleccion":"-","categoriaEducativaMicroleccion":"%s","tipoMicroleccion":"%s","bloqueadoMicroleccion":%s,
                 "tituloQuizMicroleccion":"Quiz","puntajeAprobatorioMicroleccion":60,"puntosRecompensaMicroleccion":20}
                """.formatted(titulo, categoria, tipo, bloqueado);
        String creado = mockMvc.perform(post("/microlecciones/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(creado, "$.idMicroleccion");
        contenidos.put(titulo, id);
        java.util.List<Integer> ids = new java.util.ArrayList<>();
        for (int i = 1; i <= numeroPreguntas; i++) {
            String pregunta = mockMvc.perform(post("/preguntas-quiz/web").header("Authorization", "Bearer " + tokenAdmin)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idMicroleccion\":" + id + ",\"enunciadoPreguntaQuiz\":\"Question " + i + "\",\"opcionAPreguntaQuiz\":\"Yes\","
                                    + "\"opcionBPreguntaQuiz\":\"No\",\"opcionCorrectaPreguntaQuiz\":\"A\",\"explicacionPreguntaQuiz\":\"Because\"}"))
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
            ids.add(JsonPath.read(pregunta, "$.idPreguntaQuiz"));
        }
        preguntas.put(titulo, ids);
    }

    @Given("a micro-lesson {string} of the category {string} with {int} quiz questions")
    public void unaMicroleccion(String titulo, String categoria, int numeroPreguntas) throws Exception {
        crearContenido(titulo, "leccion", categoria, false, numeroPreguntas);
    }

    @Given("a tip {string} of the category {string}")
    public void unConsejo(String titulo, String categoria) throws Exception {
        crearContenido(titulo, "tip", categoria, false, 0);
    }

    @Given("a locked micro-lesson {string} of the category {string}")
    public void unaMicroleccionBloqueada(String titulo, String categoria) throws Exception {
        crearContenido(titulo, "leccion", categoria, true, 1);
    }

    @When("the user opens the category {string}")
    public void abreLaCategoria(String categoria) throws Exception {
        respuesta = enviar(get("/aprende").param("categoria", categoria));
    }

    @Then("only content of the category {string} is shown, including {string}")
    public void soloEsaCategoria(String categoria, String titulo) throws Exception {
        String cuerpo = respuesta.andExpect(status().isOk()).andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat((java.util.List<String>) JsonPath.read(cuerpo, "$[*].categoria")).isNotEmpty().allMatch(categoria::equalsIgnoreCase);
        assertThat((java.util.List<String>) JsonPath.read(cuerpo, "$[*].titulo")).contains(titulo);
    }

    @When("the user marks {string} as favorite")
    public void marcaFavorito(String titulo) throws Exception {
        respuesta = enviar(put("/aprende/" + contenidos.get(titulo) + "/favorito"));
        respuesta.andExpect(status().isOk());
    }

    @Then("{string} is in the favorites list")
    public void estaEnFavoritos(String titulo) throws Exception {
        enviar(get("/aprende").param("favoritos", "true")).andExpect(status().isOk())
                .andExpect(jsonPath("$[*].titulo", hasItem(titulo)));
    }

    @Then("the favorites list is empty")
    public void favoritosVacios() throws Exception {
        enviar(get("/aprende").param("favoritos", "true")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @When("the user opens the micro-lesson {string}")
    public void abreLaMicroleccion(String titulo) throws Exception {
        respuesta = enviar(get("/aprende/" + contenidos.get(titulo)));
    }

    @Then("the content and {int} quiz questions without their answers are shown")
    public void contenidoYPreguntas(int numeroPreguntas) throws Exception {
        String cuerpo = respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Save a little every day."))
                .andExpect(jsonPath("$.preguntas", hasSize(numeroPreguntas)))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(cuerpo).doesNotContain("Because").doesNotContain("opcionCorrecta");
    }

    @When("the user answers all the questions of {string} correctly")
    public void respondeBien(String titulo) throws Exception {
        String respuestas = preguntas.get(titulo).stream().map(id -> "\"" + id + "\":\"a\"")
                .collect(java.util.stream.Collectors.joining(","));
        respuesta = enviar(post("/aprende/" + contenidos.get(titulo) + "/quiz").contentType(MediaType.APPLICATION_JSON)
                .content("{\"respuestas\":{" + respuestas + "}}"));
    }

    @Then("the score is {int}, the quiz is passed and each answer has its feedback")
    public void puntajeYRetroalimentacion(int puntaje) throws Exception {
        respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$.puntaje").value(puntaje))
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.retroalimentacion[*].acerto", hasItem(true)))
                .andExpect(jsonPath("$.retroalimentacion[0].explicacion").value("Because"));
    }
}
