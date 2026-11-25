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

// Pruebas de INTEGRACIÓN del Centro de Retos y Logros:
// US26 (retos activos y progreso), US27 (unirse), US28 (requisitos), US29 (reto personal) y logros automáticos.
// El Reloj es simulado: así se prueba "un progreso por día" pasando a "mañana" sin esperar.
@SpringBootTest
@AutoConfigureMockMvc
class RetosIntegracionTests {

    private static final LocalDate HOY = LocalDate.of(2026, 12, 1);

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
    private int idUsuario;

    @BeforeEach
    void preparar() throws Exception {
        when(reloj.hoy()).thenReturn(HOY);
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        tokenAdmin = api.login(api.registrarAdmin());
        String correo = api.registrarUsuario(); // nace el 01/01/2000: tiene 26 años
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        crearLogroSiNoExiste("Retador", 40);
        crearLogroSiNoExiste("Primer gasto registrado", 10);
        // Al abrir el Centro se entregan los logros que el usuario ya cumple (ej. "Primeros Pasos" por su saldo):
        // así las pruebas miden solo los puntos de lo que hacen después
        resumen(token);
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

    // Reto oficial creado por el ADMIN
    private int retoOficial(String titulo, LocalDate inicio, LocalDate fin, Integer duracion, int edadMinima, int puntos) throws Exception {
        String json = """
                {"titulo":"%s","descripcion":"Reto de prueba","montoObjetivo":70,"puntosRecompensa":%d,
                 "fechaInicio":"%s","fechaFin":"%s","saldoInicial":0,"edadMinima":%d,"saldoProyectado":70%s}
                """.formatted(titulo, puntos, inicio, fin, edadMinima, duracion == null ? "" : ",\"duracionDias\":" + duracion);
        String respuesta = mockMvc.perform(post("/desafios/web").header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.idDesafio");
    }

    private ResultActions unirme(String tokenUsado, int idDesafio) throws Exception {
        return mockMvc.perform(post("/retos/" + idDesafio + "/unirme").header("Authorization", "Bearer " + tokenUsado)
                .header("Accept-Language", "es-419"));
    }

    private int idParticipacion(ResultActions resultado) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.idUsuarioDesafio");
    }

    private ResultActions progreso(String tokenUsado, int idParticipacion) throws Exception {
        return mockMvc.perform(post("/retos/participaciones/" + idParticipacion + "/progreso")
                .header("Authorization", "Bearer " + tokenUsado).header("Accept-Language", "es-419"));
    }

    private String resumen(String tokenUsado) throws Exception {
        return mockMvc.perform(get("/retos/resumen").header("Authorization", "Bearer " + tokenUsado))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private int puntos() throws Exception {
        String yo = mockMvc.perform(get("/usuarios/yo").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(yo, "$.puntosGamificacionUsuario");
    }

    // ---------- Pruebas ----------

    @Test
    @DisplayName("US27 + US26: unirse a un reto vigente y verlo en mis retos activos (Día 0 de 7)")
    void unirseYVerloActivo() throws Exception {
        int idReto = retoOficial("Semana sin delivery", HOY, HOY.plusDays(30), 7, 18, 50);
        unirme(token, idReto).andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoDesafio").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.diaActual").value(0))
                .andExpect(jsonPath("$.duracionDias").value(7));

        String resumen = resumen(token);
        assertThat((List<String>) JsonPath.read(resumen, "$.activos[*].titulo")).containsExactly("Semana sin delivery");
        // Ya inscrito: no puede volver a unirse
        unirme(token, idReto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ya estás participando en este reto")));
    }

    @Test
    @DisplayName("US28: si no cumple la edad mínima, la lista dice qué le falta y no puede unirse")
    void requisitoDeEdad() throws Exception {
        int idReto = retoOficial("Solo mayores de 30", HOY, HOY.plusDays(10), 5, 30, 20);
        String disponibles = mockMvc.perform(get("/retos/disponibles").header("Authorization", "Bearer " + token)
                        .header("Accept-Language", "es-419"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Boolean> puede = JsonPath.read(disponibles, "$[?(@.idDesafio == " + idReto + ")].puedeUnirse");
        List<List<String>> faltan = JsonPath.read(disponibles, "$[?(@.idDesafio == " + idReto + ")].requisitosFaltantes");
        assertThat(puede).containsExactly(false);
        assertThat(faltan.get(0)).containsExactly("Debes tener al menos 30 años (tienes 26)");

        unirme(token, idReto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Debes tener al menos 30 años")));
    }

    @Test
    @DisplayName("US28: no puede unirse a un reto que aún no empieza ni a uno al que ya no le alcanzan los días")
    void requisitosDeFechas() throws Exception {
        int futuro = retoOficial("Reto de enero", HOY.plusDays(31), HOY.plusDays(60), 7, 18, 20);
        unirme(token, futuro).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("El reto empieza el")));
        int corto = retoOficial("Quedan 3 días", HOY.minusDays(10), HOY.plusDays(2), 7, 18, 20);
        unirme(token, corto).andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Ya no alcanzan los días")));
    }

    @Test
    @DisplayName("Progreso diario: +20 XP, solo una vez por día; al día siguiente sí se puede")
    void unProgresoPorDia() throws Exception {
        int idReto = retoOficial("Tres días", HOY, HOY.plusDays(20), 3, 18, 30);
        int idParticipacion = idParticipacion(unirme(token, idReto));
        int puntosAntes = puntos();

        progreso(token, idParticipacion).andExpect(status().isOk())
                .andExpect(jsonPath("$.xpGanado").value(20))
                .andExpect(jsonPath("$.diaActual").value(1))
                .andExpect(jsonPath("$.completado").value(false));
        progreso(token, idParticipacion).andExpect(status().isConflict())
                .andExpect(content().string("Ya registraste tu progreso de hoy"));
        assertThat(puntos()).isEqualTo(puntosAntes + 20);

        when(reloj.hoy()).thenReturn(HOY.plusDays(1)); // mañana
        progreso(token, idParticipacion).andExpect(status().isOk()).andExpect(jsonPath("$.diaActual").value(2));
        assertThat((Integer) JsonPath.read(resumen(token), "$.racha")).isEqualTo(2);
    }

    @Test
    @DisplayName("Superar un reto oficial: puntos de recompensa, logro Retador y notificaciones")
    void superarRetoOficial() throws Exception {
        int idReto = retoOficial("Dos días", HOY, HOY.plusDays(5), 2, 18, 50);
        int idParticipacion = idParticipacion(unirme(token, idReto));
        int puntosAntes = puntos();

        progreso(token, idParticipacion).andExpect(status().isOk());
        when(reloj.hoy()).thenReturn(HOY.plusDays(1));
        String respuesta = progreso(token, idParticipacion).andExpect(status().isOk())
                .andExpect(jsonPath("$.completado").value(true))
                .andExpect(jsonPath("$.puntosRecompensa").value(50))
                .andReturn().getResponse().getContentAsString();
        assertThat((List<String>) JsonPath.read(respuesta, "$.logrosDesbloqueados")).contains("Retador");
        // 20 + 20 de los días, 50 de recompensa y 40 del logro Retador
        assertThat(puntos()).isEqualTo(puntosAntes + 20 + 20 + 50 + 40);

        String resumen = resumen(token);
        assertThat((Integer) JsonPath.read(resumen, "$.retosSuperados")).isEqualTo(1);
        assertThat((List<String>) JsonPath.read(resumen, "$.historial[*].estadoDesafio")).containsExactly("COMPLETADO");
        String notificaciones = mockMvc.perform(get("/notificaciones").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        assertThat((List<String>) JsonPath.read(notificaciones, "$[*].tipo")).contains("reto", "logro");
    }

    @Test
    @DisplayName("US29: un reto personal queda inscrito, solo lo ve su creador y no da puntos de recompensa")
    void retoPersonal() throws Exception {
        String respuesta = mockMvc.perform(post("/retos/personal").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"sin_delivery\",\"duracionDias\":1,\"montoObjetivo\":30}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Días sin delivery"))
                .andExpect(jsonPath("$.oficial").value(false))
                .andReturn().getResponse().getContentAsString();
        int idParticipacion = JsonPath.read(respuesta, "$.idUsuarioDesafio");
        int idDesafio = JsonPath.read(respuesta, "$.idDesafio");

        String completado = progreso(token, idParticipacion).andExpect(status().isOk())
                .andExpect(jsonPath("$.completado").value(true))
                .andExpect(jsonPath("$.puntosRecompensa").value(0))
                .andReturn().getResponse().getContentAsString();
        // Un reto personal de 1 día no puede dar la medalla "Retador" (si no, cualquiera ganaría 40 puntos en un minuto)
        assertThat((List<String>) JsonPath.read(completado, "$.logrosDesbloqueados")).doesNotContain("Retador");

        String tokenOtro = api.login(api.registrarUsuario());
        mockMvc.perform(get("/desafios/" + idDesafio).header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
        String listaOtro = mockMvc.perform(get("/desafios").header("Authorization", "Bearer " + tokenOtro))
                .andReturn().getResponse().getContentAsString();
        assertThat((List<Integer>) JsonPath.read(listaOtro, "$[*].idDesafio")).doesNotContain(idDesafio);
    }

    @Test
    @DisplayName("US29: el reto personal valida tipo, duración y meta")
    void retoPersonalInvalido() throws Exception {
        String[] invalidos = {
                "{\"tipo\":\"otro\",\"duracionDias\":7,\"montoObjetivo\":30}",
                "{\"tipo\":\"ahorro_libre\",\"duracionDias\":0,\"montoObjetivo\":30}",
                "{\"tipo\":\"ahorro_libre\",\"duracionDias\":120,\"montoObjetivo\":30}",
                "{\"tipo\":\"ahorro_libre\",\"duracionDias\":7,\"montoObjetivo\":0}"};
        for (String json : invalidos) {
            mockMvc.perform(post("/retos/personal").header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Seguridad: nadie registra progreso en el reto de otro ni se pone el progreso a mano")
    void seguridad() throws Exception {
        int idReto = retoOficial("Reto seguro", HOY, HOY.plusDays(9), 5, 18, 20);
        int idParticipacion = idParticipacion(unirme(token, idReto));
        String tokenOtro = api.login(api.registrarUsuario());

        progreso(tokenOtro, idParticipacion).andExpect(status().isForbidden());
        // Los endpoints antiguos donde el progreso venía en el JSON ahora son solo para ADMIN
        mockMvc.perform(put("/usuariosdesafios/actualiza").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuarioDesafio\":" + idParticipacion + ",\"idUsuario\":" + idUsuario + ",\"idDesafio\":" + idReto
                                + ",\"saldoInicial\":0,\"montoProgresoActual\":70,\"porcentajeProgreso\":100,\"estadoDesafio\":\"COMPLETADO\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/usuariosdesafios/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idUsuario\":" + idUsuario + ",\"idDesafio\":" + idReto
                                + ",\"saldoInicial\":0,\"montoProgresoActual\":70,\"porcentajeProgreso\":100,\"estadoDesafio\":\"COMPLETADO\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Si pasa la fecha fin sin cumplir todos los días, el reto queda VENCIDO; también se puede abandonar")
    void vencidoYAbandonado() throws Exception {
        int corto = retoOficial("Vence pronto", HOY, HOY.plusDays(2), 3, 18, 20);
        int largo = retoOficial("Para abandonar", HOY, HOY.plusDays(20), 5, 18, 20);
        idParticipacion(unirme(token, corto));
        int idLargo = idParticipacion(unirme(token, largo));

        mockMvc.perform(put("/retos/participaciones/" + idLargo + "/abandonar").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estadoDesafio").value("ABANDONADO"));

        when(reloj.hoy()).thenReturn(HOY.plusDays(3)); // ya pasó la fecha fin del reto corto
        String resumen = resumen(token);
        assertThat((List<String>) JsonPath.read(resumen, "$.historial[*].estadoDesafio")).contains("VENCIDO", "ABANDONADO");
        assertThat((List<?>) JsonPath.read(resumen, "$.activos")).isEmpty();
    }

    @Test
    @DisplayName("Si dos peticiones revisan los logros al mismo tiempo, el logro se otorga una sola vez")
    void logroNoSeDuplicaConPeticionesSimultaneas() throws Exception {
        crearLogroSiNoExiste("Primeros Pasos", 10); // se cumple con saldo >= 10 (el usuario tiene 1000)
        java.util.concurrent.ExecutorService hilos = java.util.concurrent.Executors.newFixedThreadPool(4);
        java.util.List<java.util.concurrent.Future<String>> respuestas = new java.util.ArrayList<>();
        for (int i = 0; i < 4; i++) {
            respuestas.add(hilos.submit(() -> resumen(token)));
        }
        for (java.util.concurrent.Future<String> respuesta : respuestas) {
            respuesta.get(); // espera a las 4
        }
        hilos.shutdown();

        String notificaciones = mockMvc.perform(get("/notificaciones").header("Authorization", "Bearer " + token))
                .andReturn().getResponse().getContentAsString();
        List<String> mensajes = JsonPath.read(notificaciones, "$[*].mensaje");
        assertThat(mensajes.stream().filter(m -> m.contains("Primeros Pasos")).count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Logro automático: el primer gasto desbloquea \"Primer gasto registrado\" y suma sus puntos")
    void logroPrimerGasto() throws Exception {
        int idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        int idComercio = api.crearComercio(tokenAdmin, idCategoria);
        int puntosAntes = puntos();
        api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Primer gasto", "5", LocalDate.now()));

        String medallas = mockMvc.perform(get("/retos/medallas").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Boolean> desbloqueada = JsonPath.read(medallas, "$[?(@.nombre == 'Primer gasto registrado')].desbloqueada");
        assertThat(desbloqueada).isNotEmpty().allMatch(Boolean::booleanValue);
        assertThat(puntos()).isGreaterThanOrEqualTo(puntosAntes + 10);
    }
}
