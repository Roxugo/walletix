package pe.edu.upc.walletix.integracion;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pe.edu.upc.walletix.config.Reloj;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN de la sección "Aprende":
// US31 (consejos por categoría), US32 (favoritos), US33 (contenido bloqueado), US34 (microlecciones) y US35 (quiz).
@SpringBootTest
@AutoConfigureMockMvc
class AprendeIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;
    @MockitoBean
    private Reloj reloj;

    private ApiPruebas api;
    private String tokenAdmin;
    private String token;
    private static int contador = 0;

    @BeforeEach
    void preparar() throws Exception {
        when(reloj.hoy()).thenReturn(LocalDate.now());
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        tokenAdmin = api.login(api.registrarAdmin());
        token = api.login(api.registrarUsuario());
        crearLogroSiNoExiste("Estudioso", 20);
        crearLogroSiNoExiste("Lector Financiero", 10);
        // Entrega los logros que el usuario ya cumple al nacer (ej. "Primeros Pasos"): así se miden solo los del quiz
        mockMvc.perform(get("/retos/resumen").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    // ---------- Ayudantes ----------

    private void crearLogroSiNoExiste(String nombre, int puntos) throws Exception {
        String lista = mockMvc.perform(get("/logros").header("Authorization", "Bearer " + tokenAdmin))
                .andReturn().getResponse().getContentAsString();
        if (!((List<String>) JsonPath.read(lista, "$[*].nombreLogro")).contains(nombre)) {
            mockMvc.perform(post("/logros/web").header("Authorization", "Bearer " + tokenAdmin)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombreLogro\":\"" + nombre + "\",\"descripcionLogro\":\"Prueba\",\"urlIconoLogro\":\"x\",\"puntosLogro\":" + puntos + "}"))
                    .andExpect(status().isCreated());
        }
    }

    // Microlección creada por el ADMIN
    private int contenido(String titulo, String tipo, String categoria, boolean bloqueado, int aprobatorio, int puntos) throws Exception {
        String json = """
                {"tituloMicroleccion":"%s","descripcionMicroleccion":"Descripción de prueba",
                 "contenidoMicroleccion":"El interés compuesto hace crecer tu ahorro mes a mes.","urlMediaMicroleccion":"-",
                 "categoriaEducativaMicroleccion":"%s","tipoMicroleccion":"%s","bloqueadoMicroleccion":%s,
                 "tituloQuizMicroleccion":"Quiz","puntajeAprobatorioMicroleccion":%d,"puntosRecompensaMicroleccion":%d}
                """.formatted(titulo, categoria, tipo, bloqueado, aprobatorio, puntos);
        String respuesta = mockMvc.perform(post("/microlecciones/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.idMicroleccion");
    }

    private int pregunta(int idMicroleccion, String correcta) throws Exception {
        String json = """
                {"idMicroleccion":%d,"enunciadoPreguntaQuiz":"¿Pregunta %d?","opcionAPreguntaQuiz":"Uno","opcionBPreguntaQuiz":"Dos",
                 "opcionCPreguntaQuiz":"Tres","opcionCorrectaPreguntaQuiz":"%s","explicacionPreguntaQuiz":"Porque sí"}
                """.formatted(idMicroleccion, ++contador, correcta);
        String respuesta = mockMvc.perform(post("/preguntas-quiz/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.idPreguntaQuiz");
    }

    private ResultActions quiz(int idMicroleccion, String respuestas) throws Exception {
        return mockMvc.perform(post("/aprende/" + idMicroleccion + "/quiz").header("Authorization", "Bearer " + token)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON)
                .content("{\"respuestas\":{" + respuestas + "}}"));
    }

    private String listar(String parametros) throws Exception {
        return mockMvc.perform(get("/aprende" + parametros).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private int puntos() throws Exception {
        String yo = mockMvc.perform(get("/usuarios/yo").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(yo, "$.puntosGamificacionUsuario");
    }

    // ---------- Pruebas ----------

    @Test
    @DisplayName("US31 + US34: filtrar por categoría (sin importar tildes) y abrir una microlección sin ver las respuestas")
    void categoriaYMicroleccion() throws Exception {
        int leccion = contenido("Invertir desde cero", "leccion", "Inversión", false, 60, 20);
        int tip = contenido("Anota tus gastos", "tip", "habitos", false, 0, 0);
        pregunta(leccion, "B");

        String inversion = listar("?categoria=inversion");
        assertThat((List<Integer>) JsonPath.read(inversion, "$[*].idMicroleccion")).contains(leccion).doesNotContain(tip);
        assertThat((List<Integer>) JsonPath.read(listar("?buscar=INTERES compuesto"), "$[*].idMicroleccion")).contains(leccion, tip);

        String abierta = mockMvc.perform(get("/aprende/" + leccion).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido", containsString("interés compuesto")))
                .andExpect(jsonPath("$.resumen.minutosLectura").value(1))
                .andExpect(jsonPath("$.preguntas.length()").value(1))
                .andExpect(jsonPath("$.preguntas[0].opciones.length()").value(3))
                .andReturn().getResponse().getContentAsString();
        // El navegador nunca recibe la respuesta correcta ni la explicación antes de responder
        assertThat(abierta).doesNotContain("opcionCorrecta").doesNotContain("Porque sí");
    }

    @Test
    @DisplayName("US35: la API califica el quiz, da retroalimentación y los puntos solo la primera vez que se aprueba")
    void quizCalificadoPorLaApi() throws Exception {
        int leccion = contenido("Regla 50/30/20", "leccion", "presupuesto", false, 60, 25);
        int p1 = pregunta(leccion, "A");
        int p2 = pregunta(leccion, "c");
        int antes = puntos();

        // 1 de 2 correctas = 50 (no llega a 60): sin puntos de la lección, pero ya ganó "Lector Financiero" por intentarlo
        String desaprobado = quiz(leccion, "\"%d\":\"a\",\"%d\":\"b\"".formatted(p1, p2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.puntaje").value(50))
                .andExpect(jsonPath("$.aprobado").value(false))
                .andExpect(jsonPath("$.puntosGanados").value(0))
                .andExpect(jsonPath("$.retroalimentacion[1].acerto").value(false))
                .andExpect(jsonPath("$.retroalimentacion[1].respuestaCorrecta").value("c"))
                .andExpect(jsonPath("$.retroalimentacion[1].textoCorrecto").value("Tres"))
                .andExpect(jsonPath("$.retroalimentacion[1].explicacion").value("Porque sí"))
                .andReturn().getResponse().getContentAsString();
        assertThat((List<String>) JsonPath.read(desaprobado, "$.logrosDesbloqueados")).contains("Lector Financiero");

        // 2 de 2 = 100: aprueba por primera vez -> +25 de la lección y la medalla "Estudioso"
        String aprobado = quiz(leccion, "\"%d\":\"A\",\"%d\":\"c\"".formatted(p1, p2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.puntaje").value(100))
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.primeraVezAprobado").value(true))
                .andExpect(jsonPath("$.puntosGanados").value(25))
                .andReturn().getResponse().getContentAsString();
        assertThat((List<String>) JsonPath.read(aprobado, "$.logrosDesbloqueados")).contains("Estudioso");

        // Repetirlo sirve para repasar, pero ya no suma puntos
        quiz(leccion, "\"%d\":\"a\",\"%d\":\"c\"".formatted(p1, p2)).andExpect(status().isOk())
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.primeraVezAprobado").value(false))
                .andExpect(jsonPath("$.puntosGanados").value(0));
        assertThat(puntos()).isEqualTo(antes + 25 + 10 + 20); // lección + Lector Financiero + Estudioso

        List<Object> fila = JsonPath.read(listar("?categoria=presupuesto"), "$[?(@.idMicroleccion == " + leccion + ")]");
        assertThat(JsonPath.<Boolean>read(fila.get(0), "$.aprobado")).isTrue();
        assertThat(JsonPath.<Integer>read(fila.get(0), "$.mejorPuntaje")).isEqualTo(100);
    }

    @Test
    @DisplayName("US35: hay que responder todas las preguntas con una opción que exista; un consejo no tiene quiz")
    void quizInvalido() throws Exception {
        int leccion = contenido("Tarjeta de crédito", "leccion", "credito", false, 60, 20);
        int p1 = pregunta(leccion, "A");
        int p2 = pregunta(leccion, "B");
        quiz(leccion, "\"%d\":\"a\"".formatted(p1)).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Responde todas las preguntas")));
        quiz(leccion, "\"%d\":\"a\",\"%d\":\"d\"".formatted(p1, p2)).andExpect(status().isBadRequest()); // no hay opción D
        int tip = contenido("Un tip", "tip", "habitos", false, 0, 0);
        quiz(tip, "").andExpect(status().isBadRequest());
        quiz(999999, "").andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("US32: marcar un consejo como favorito, verlo en la lista y quitarlo")
    void favoritos() throws Exception {
        int tip = contenido("Ahorra lo que sobra", "tip", "ahorro", false, 0, 0);
        mockMvc.perform(put("/aprende/" + tip + "/favorito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.favorito").value(true)).andExpect(jsonPath("$.totalFavoritos").value(1));
        assertThat((List<Integer>) JsonPath.read(listar("?favoritos=true"), "$[*].idMicroleccion")).containsExactly(tip);

        mockMvc.perform(put("/aprende/" + tip + "/favorito").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.favorito").value(false)).andExpect(jsonPath("$.totalFavoritos").value(0));
        assertThat((List<Integer>) JsonPath.read(listar("?favoritos=true"), "$[*].idMicroleccion")).isEmpty();
    }

    @Test
    @DisplayName("US33: el contenido bloqueado muestra qué hacer y se abre al superar un reto oficial")
    void contenidoBloqueado() throws Exception {
        int bloqueada = contenido("Metas SMART", "leccion", "ahorro", true, 60, 30);
        int p1 = pregunta(bloqueada, "A");

        List<Object> fila = JsonPath.read(listar("?categoria=ahorro"), "$[?(@.idMicroleccion == " + bloqueada + ")]");
        assertThat(JsonPath.<Boolean>read(fila.get(0), "$.bloqueado")).isTrue();
        assertThat(JsonPath.<String>read(fila.get(0), "$.condicion")).contains("Supera un reto oficial");
        mockMvc.perform(get("/aprende/" + bloqueada).header("Authorization", "Bearer " + token).header("Accept-Language", "es-419"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Este contenido está bloqueado. Supera un reto oficial")));
        quiz(bloqueada, "\"%d\":\"a\"".formatted(p1)).andExpect(status().isForbidden());

        // Supera un reto oficial de 1 día
        LocalDate hoy = LocalDate.now();
        String reto = mockMvc.perform(post("/desafios/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Reto rápido","descripcion":"Prueba","montoObjetivo":10,"puntosRecompensa":5,
                                 "fechaInicio":"%s","fechaFin":"%s","saldoInicial":0,"edadMinima":0,"saldoProyectado":10,"duracionDias":1}
                                """.formatted(hoy, hoy.plusDays(5))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int idDesafio = JsonPath.read(reto, "$.idDesafio");
        String participacion = mockMvc.perform(post("/retos/" + idDesafio + "/unirme").header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int idParticipacion = JsonPath.read(participacion, "$.idUsuarioDesafio");
        mockMvc.perform(post("/retos/participaciones/" + idParticipacion + "/progreso").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completado").value(true));

        mockMvc.perform(get("/aprende/" + bloqueada).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resumen.bloqueado").value(false));
    }

    @Test
    @DisplayName("Tip de 30 segundos: devuelve un consejo desbloqueado")
    void tipDelDia() throws Exception {
        contenido("Tip del día", "tip", "habitos", false, 0, 0);
        mockMvc.perform(get("/aprende/tip-del-dia").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumen.tipo").value("tip"))
                .andExpect(jsonPath("$.resumen.bloqueado").value(false));
    }

    @Test
    @DisplayName("Seguridad: el USUARIO no ve las respuestas del catálogo ni registra su propio puntaje")
    void seguridad() throws Exception {
        int leccion = contenido("Seguridad", "leccion", "ahorro", false, 60, 20);
        pregunta(leccion, "A");
        mockMvc.perform(get("/preguntas-quiz").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        mockMvc.perform(get("/preguntas-quiz/microleccion/" + leccion).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/microlecciones/" + leccion).header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
        mockMvc.perform(post("/intentos-quiz/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":1,\"idMicroleccion\":" + leccion + ",\"puntajeIntentoQuizUsuario\":100}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/aprende")).andExpect(status().isUnauthorized());
    }
}
