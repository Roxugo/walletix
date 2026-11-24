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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de INTEGRACIÓN de las reglas de validación:
// - Presupuestos: año entre el actual y +5, monto hasta S/ 1 000 000, uno solo por categoría y mes
// - Gastos e ingresos: fecha desde el 01/01 del año anterior hasta hoy
// - Metas de ahorro: al crearlas, la fecha límite no puede estar en el pasado
@SpringBootTest
@AutoConfigureMockMvc
class ValidacionesIntegracionTests {

    private static final int ANIO_ACTUAL = LocalDate.now().getYear();
    private static final int MES_ACTUAL = LocalDate.now().getMonthValue();

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
    private int idComercio;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoria = api.crearCategoria(token, idUsuario, "gasto");
        idComercio = api.crearComercio(api.login(api.registrarAdmin()), idCategoria);
    }

    private ResultActions enviar(String metodo, String ruta, String json) throws Exception {
        var peticion = metodo.equals("PUT") ? put(ruta) : post(ruta);
        return mockMvc.perform(peticion.header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String presupuesto(int idPresupuesto, int mes, int anio, String monto) {
        return """
                {"idPresupuesto":%d,"mes":%d,"anio":%d,"montoAsignado":%s,"idUsuario":%d,"idCategoria":%d}
                """.formatted(idPresupuesto, mes, anio, monto, idUsuario, idCategoria);
    }

    // ---------- Presupuestos ----------

    @Test
    @DisplayName("Presupuesto: el año debe estar entre el actual y 5 años después")
    void presupuesto_rangoDeAnio() throws Exception {
        String mensaje = "El año debe estar entre " + ANIO_ACTUAL + " y " + (ANIO_ACTUAL + 5);
        for (int anio : new int[]{1990, ANIO_ACTUAL - 1, ANIO_ACTUAL + 6, 20000}) {
            enviar("POST", "/presupuestos/web", presupuesto(0, 1, anio, "100"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mensaje));
        }
        enviar("POST", "/presupuestos/web", presupuesto(0, 1, ANIO_ACTUAL + 5, "100"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Presupuesto: el monto no puede pasar de S/ 1 000 000")
    void presupuesto_montoMaximo() throws Exception {
        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL, "1000001"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El monto asignado no puede ser mayor a S/ 1 000 000"));
        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL, "1000000"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Presupuesto: no se puede repetir la misma categoría en el mismo mes y año (409)")
    void presupuesto_duplicado() throws Exception {
        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL, "400")).andExpect(status().isCreated());

        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL, "800"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya tienes un presupuesto para esa categoría en ese mes"));
    }

    @Test
    @DisplayName("Presupuesto: la misma categoría en otro mes u otro año sí se permite")
    void presupuesto_otroMesUOtroAnio() throws Exception {
        int otroMes = MES_ACTUAL == 12 ? 1 : MES_ACTUAL + 1;
        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL, "400")).andExpect(status().isCreated());
        enviar("POST", "/presupuestos/web", presupuesto(0, otroMes, ANIO_ACTUAL, "400")).andExpect(status().isCreated());
        enviar("POST", "/presupuestos/web", presupuesto(0, MES_ACTUAL, ANIO_ACTUAL + 1, "400")).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Presupuesto: editar el monto no cuenta como duplicado, pero mover a un mes ocupado sí")
    void presupuesto_editar() throws Exception {
        int otroMes = MES_ACTUAL == 12 ? 1 : MES_ACTUAL + 1;
        int idA = api.crearPresupuesto(token, idUsuario, idCategoria, "400");               // mes actual
        String cuerpoB = enviar("POST", "/presupuestos/web", presupuesto(0, otroMes, ANIO_ACTUAL, "200"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int idB = JsonPath.read(cuerpoB, "$.idPresupuesto");

        // Cambiar solo el monto de A: el único presupuesto de ese mes es él mismo
        enviar("PUT", "/presupuestos/actualiza", presupuesto(idA, MES_ACTUAL, ANIO_ACTUAL, "450"))
                .andExpect(status().isOk());
        // Mover B al mes de A: ahí ya está A
        enviar("PUT", "/presupuestos/actualiza", presupuesto(idB, MES_ACTUAL, ANIO_ACTUAL, "200"))
                .andExpect(status().isConflict());
    }

    // ---------- Gastos e ingresos ----------

    @Test
    @DisplayName("Gasto: se acepta desde el 01/01 del año anterior; antes de eso se rechaza")
    void gasto_fechaMinima() throws Exception {
        LocalDate primerDiaPermitido = LocalDate.of(ANIO_ACTUAL - 1, 1, 1);
        enviar("POST", "/gastos/web", api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Viejo", "10",
                primerDiaPermitido.minusDays(1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha no puede ser anterior al 01/01/" + (ANIO_ACTUAL - 1)));
        enviar("POST", "/gastos/web", api.jsonGasto(0, idUsuario, idCategoria, idComercio, "Permitido", "10",
                primerDiaPermitido))
                .andExpect(status().isCreated());
    }

    private String ingreso(int idCategoriaIngreso, LocalDate fecha) {
        return """
                {"idUsuario":%d,"idCategoria":%d,"monto":100,"fecha":"%s","tipoIngreso":"fijo",
                 "frecuencia":"mensual","fuente":"Sueldo"}
                """.formatted(idUsuario, idCategoriaIngreso, fecha);
    }

    @Test
    @DisplayName("Ingreso: no puede ser futuro ni anterior al 01/01 del año anterior")
    void ingreso_rangoDeFechas() throws Exception {
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        enviar("POST", "/ingresos/web", ingreso(idCategoriaIngreso, LocalDate.now().plusDays(1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha del ingreso no puede ser futura"));
        enviar("POST", "/ingresos/web", ingreso(idCategoriaIngreso, LocalDate.of(ANIO_ACTUAL - 2, 12, 31)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha no puede ser anterior al 01/01/" + (ANIO_ACTUAL - 1)));
        enviar("POST", "/ingresos/web", ingreso(idCategoriaIngreso, LocalDate.now()))
                .andExpect(status().isCreated());
    }

    // ---------- Metas de ahorro ----------

    private String meta(LocalDate fechaLimite) {
        return """
                {"titulo":"Viaje","montoObjetivo":2000,"montoActual":0,"fechaLimite":"%s",
                 "estadoMetaAhorro":"activa","idUsuario":%d}
                """.formatted(fechaLimite, idUsuario);
    }

    @Test
    @DisplayName("Meta de ahorro: al crearla, la fecha límite no puede estar en el pasado")
    void meta_fechaLimitePasada() throws Exception {
        enviar("POST", "/metas-ahorro/web", meta(LocalDate.now().minusDays(1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha límite no puede estar en el pasado"));
        enviar("POST", "/metas-ahorro/web", meta(LocalDate.now().plusMonths(6)))
                .andExpect(status().isCreated());
    }
}
