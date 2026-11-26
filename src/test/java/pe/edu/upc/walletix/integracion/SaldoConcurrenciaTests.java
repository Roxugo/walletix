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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN del saldo con peticiones simultáneas y montos con céntimos.
// Encontrado en la prueba manual en producción: 3 gastos enviados a la vez se aceptaban aunque el saldo
// solo alcanzaba para 1, y el saldo quedaba restando uno solo (actualizaciones perdidas).
@SpringBootTest
@AutoConfigureMockMvc
class SaldoConcurrenciaTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    private ApiPruebas api;
    private String token;
    private int idUsuario;
    private int idCategoria;
    private int idCategoriaIngreso;
    private int idComercio;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String tokenAdmin = api.login(api.registrarAdmin());
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        idComercio = api.crearComercio(tokenAdmin, idCategoria);
    }

    private ResultActions gasto(String monto) throws Exception {
        return mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON)
                .content(api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra", monto, LocalDate.now())));
    }

    // Lo que el usuario gastó según sus gastos activos
    private BigDecimal totalGastado() throws Exception {
        String json = mockMvc.perform(get("/gastos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        List<Object> montos = JsonPath.read(json, "$[*].montoGasto");
        return montos.stream().map(m -> new BigDecimal(m.toString())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    @DisplayName("US11: 5 gastos simultáneos con saldo para 3 → se aceptan 3, se rechazan 2 y el saldo cuadra")
    void gastosSimultaneosNoGastanDosVecesElMismoDinero() throws Exception {
        BigDecimal inicial = api.saldo(token);
        // Cada gasto es un tercio del saldo (un poco menos): alcanzan exactamente 3
        BigDecimal monto = inicial.divide(BigDecimal.valueOf(3), 2, java.math.RoundingMode.DOWN);
        ExecutorService hilos = Executors.newFixedThreadPool(5);
        List<Future<Integer>> codigos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            codigos.add(hilos.submit(() -> gasto(monto.toPlainString()).andReturn().getResponse().getStatus()));
        }
        int aceptados = 0;
        int rechazados = 0;
        for (Future<Integer> codigo : codigos) {
            int estado = codigo.get();
            if (estado == 201) aceptados++;
            if (estado == 400) rechazados++;
        }
        hilos.shutdown();

        assertThat(aceptados).isEqualTo(3);
        assertThat(rechazados).isEqualTo(2);
        // El saldo es exactamente el inicial menos lo que dicen los gastos guardados
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(totalGastado()));
        assertThat(api.saldo(token).signum()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("US14: eliminar el mismo gasto dos veces a la vez devuelve el dinero una sola vez")
    void eliminarDosVecesALaVez() throws Exception {
        BigDecimal inicial = api.saldo(token);
        int idGasto = api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra", "40", LocalDate.now()));
        ExecutorService hilos = Executors.newFixedThreadPool(2);
        List<Future<Integer>> codigos = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            codigos.add(hilos.submit(() -> mockMvc.perform(delete("/gastos/" + idGasto).header("Authorization", "Bearer " + token))
                    .andReturn().getResponse().getStatus()));
        }
        for (Future<Integer> codigo : codigos) {
            codigo.get();
        }
        hilos.shutdown();
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial);
    }

    @Test
    @DisplayName("US13: dos ediciones simultáneas del mismo gasto dejan el saldo de acuerdo con el monto final")
    void edicionesSimultaneasDelMismoGasto() throws Exception {
        BigDecimal inicial = api.saldo(token);
        int idGasto = api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra", "10", LocalDate.now()));
        ExecutorService hilos = Executors.newFixedThreadPool(2);
        List<Future<Integer>> codigos = new ArrayList<>();
        for (String monto : List.of("20", "30")) {
            codigos.add(hilos.submit(() -> mockMvc.perform(put("/gastos/actualiza").header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(api.jsonGasto(idGasto, idUsuario, idCategoria, idComercio, "Compra", monto, LocalDate.now())))
                    .andReturn().getResponse().getStatus()));
        }
        for (Future<Integer> codigo : codigos) {
            assertThat(codigo.get()).isEqualTo(200);
        }
        hilos.shutdown();
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(totalGastado()));
    }

    @Test
    @DisplayName("US12: eliminar el mismo ingreso dos veces a la vez lo descuenta una sola vez")
    void eliminarIngresoDosVecesALaVez() throws Exception {
        BigDecimal inicial = api.saldo(token);
        int idIngreso = api.crearIngreso(token, idUsuario, idCategoriaIngreso, "50");
        ExecutorService hilos = Executors.newFixedThreadPool(2);
        List<Future<Integer>> codigos = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            codigos.add(hilos.submit(() -> mockMvc.perform(delete("/ingresos/" + idIngreso).header("Authorization", "Bearer " + token))
                    .andReturn().getResponse().getStatus()));
        }
        for (Future<Integer> codigo : codigos) {
            codigo.get();
        }
        hilos.shutdown();
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial);
    }

    @Test
    @DisplayName("US11-US12: los montos tienen como máximo 2 decimales (si no, el saldo se descuadraba 1 céntimo)")
    void montosConCentimos() throws Exception {
        BigDecimal inicial = api.saldo(token);
        gasto("10.555").andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("como máximo 2 decimales")));
        gasto("10.50").andExpect(status().isCreated());
        gasto("10.5").andExpect(status().isCreated());
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(new BigDecimal("21.00")));
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(totalGastado()));

        mockMvc.perform(post("/ingresos/web").header("Authorization", "Bearer " + token).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"idUsuario":%d,"idCategoria":%d,"monto":1.005,"fecha":"%s","tipoIngreso":"fijo",
                                 "frecuencia":"mensual","fuente":"Sueldo"}
                                """.formatted(idUsuario, idCategoriaIngreso, LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("como máximo 2 decimales")));
        api.crearIngreso(token, idUsuario, idCategoriaIngreso, "100.25");
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(new BigDecimal("21.00")).add(new BigDecimal("100.25")));
    }

    @Test
    @DisplayName("US13: editar un gasto subiéndolo más de lo que hay de saldo no cambia nada")
    void editarSinSaldoNoCambiaNada() throws Exception {
        BigDecimal inicial = api.saldo(token);
        int idGasto = api.crearGasto(token, api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Compra", "10", LocalDate.now()));
        String demasiado = inicial.add(BigDecimal.TEN).toPlainString();
        mockMvc.perform(put("/gastos/actualiza").header("Authorization", "Bearer " + token).header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(idGasto, idUsuario, idCategoria, idComercio, "Compra", demasiado, LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("supera tu saldo")));
        assertThat(api.saldo(token)).isEqualByComparingTo(inicial.subtract(BigDecimal.TEN));
        assertThat(totalGastado()).isEqualByComparingTo("10");
    }
}
