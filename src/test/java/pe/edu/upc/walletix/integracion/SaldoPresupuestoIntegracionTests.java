package pe.edu.upc.walletix.integracion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN del saldo (US11, US12, US13, US14) y de la alerta de presupuesto (US15).
// Todos los usuarios de prueba empiezan con S/ 1000.00 de saldo (ver ApiPruebas.jsonUsuario).
@SpringBootTest
@AutoConfigureMockMvc
class SaldoPresupuestoIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;
    @Autowired
    private INotificacionService notificacionService;

    private ApiPruebas api;
    private String token;
    private int idUsuario;
    private int idCategoriaGasto;
    private int idComercio;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoriaGasto = api.crearCategoria(token, idUsuario, "gasto");
        idComercio = api.crearComercio(api.login(api.registrarAdmin()), idCategoriaGasto);
    }

    private String gasto(int idGasto, String monto) {
        return api.jsonGasto(idGasto, idUsuario, idCategoriaGasto, idComercio, "Compra", monto, LocalDate.now());
    }

    @Test
    @DisplayName("US11: registrar un gasto descuenta su monto del saldo")
    void us11_gastoDescuentaSaldo() throws Exception {
        api.crearGasto(token, gasto(0, "25.50"));
        assertThat(api.saldo(token)).isEqualByComparingTo("974.50");
    }

    @Test
    @DisplayName("US11: un gasto mayor que el saldo se rechaza y el saldo no cambia")
    void us11_gastoMayorQueSaldo() throws Exception {
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(gasto(0, "1500")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El gasto supera tu saldo disponible (S/ 1000.00)"));
        assertThat(api.saldo(token)).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("US12: registrar un ingreso suma su monto al saldo")
    void us12_ingresoSumaSaldo() throws Exception {
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        api.crearIngreso(token, idUsuario, idCategoriaIngreso, "1500.10");
        assertThat(api.saldo(token)).isEqualByComparingTo("2500.10");
    }

    @Test
    @DisplayName("US12: eliminar un ingreso ya gastado se rechaza para que el saldo no quede negativo")
    void us12_eliminarIngresoGastado() throws Exception {
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        int idIngreso = api.crearIngreso(token, idUsuario, idCategoriaIngreso, "500");   // saldo 1500
        api.crearGasto(token, gasto(0, "1400"));                                         // saldo 100

        mockMvc.perform(delete("/ingresos/" + idIngreso).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
        assertThat(api.saldo(token)).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("US13: editar el monto de un gasto ajusta el saldo con la diferencia")
    void us13_editarAjustaSaldo() throws Exception {
        int idGasto = api.crearGasto(token, gasto(0, "15"));                             // saldo 985
        mockMvc.perform(put("/gastos/actualiza").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(gasto(idGasto, "45.90")))
                .andExpect(status().isOk());
        assertThat(api.saldo(token)).isEqualByComparingTo("954.10");                     // 1000 - 45.90
    }

    @Test
    @DisplayName("US14: eliminar un gasto devuelve su monto al saldo")
    void us14_eliminarDevuelveSaldo() throws Exception {
        int idGasto = api.crearGasto(token, gasto(0, "30"));                             // saldo 970
        mockMvc.perform(delete("/gastos/" + idGasto).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        assertThat(api.saldo(token)).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("US15: sin superar el presupuesto no hay alerta")
    void us15_dentroDelPresupuesto() throws Exception {
        api.crearPresupuesto(token, idUsuario, idCategoriaGasto, "100");
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(gasto(0, "80")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertaPresupuesto").doesNotExist());
        assertThat(alertasDePresupuesto()).isEmpty();
    }

    // Solo las notificaciones de tipo "alerta" (un primer gasto también puede desbloquear un logro y avisarlo)
    private List<Notificacion> alertasDePresupuesto() {
        return notificacionService.buscarNoLeidasPorUsuario(idUsuario).stream()
                .filter(notificacion -> "alerta".equals(notificacion.getTipo()))
                .toList();
    }

    @Test
    @DisplayName("US15: el gasto que supera el presupuesto del mes trae la alerta y crea una notificación")
    void us15_superaElPresupuesto() throws Exception {
        api.crearPresupuesto(token, idUsuario, idCategoriaGasto, "100");
        api.crearGasto(token, gasto(0, "80"));

        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(gasto(0, "30")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertaPresupuesto").value(org.hamcrest.Matchers.containsString("S/ 110.00 de S/ 100.00")));

        List<Notificacion> alertas = alertasDePresupuesto();
        assertThat(alertas).hasSize(1);
        assertThat(alertas.get(0).getTipo()).isEqualTo("alerta");
        assertThat(alertas.get(0).getTitulo()).isEqualTo("Presupuesto excedido");
    }

    @Test
    @DisplayName("US15: un gasto de otra categoría no cuenta para ese presupuesto")
    void us15_otraCategoria() throws Exception {
        api.crearPresupuesto(token, idUsuario, idCategoriaGasto, "100");
        int otraCategoria = api.crearCategoria(token, idUsuario, "gasto");
        String gastoOtraCategoria = api.jsonGasto(0, idUsuario, otraCategoria, idComercio, "Otra", "150", LocalDate.now());
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(gastoOtraCategoria))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alertaPresupuesto").doesNotExist());
    }
}
