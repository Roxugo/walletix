package pe.edu.upc.walletix.integracion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pe.edu.upc.walletix.servicesinterfaces.IGastoService;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;
import pe.edu.upc.walletix.soporte.ApiPruebas;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de INTEGRACIÓN: la petición HTTP recorre todas las capas reales
// (seguridad JWT -> controller -> servicio -> repositorio JPA -> base de datos H2).
// Cada prueba indica la historia de usuario (US) que cubre.
@SpringBootTest
@AutoConfigureMockMvc
class GastosIngresosIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;
    @Autowired
    private IGastoService gastoService;

    private ApiPruebas api;
    private String token;
    private int idUsuario;
    private int idCategoriaGasto;
    private int idComercio;

    // Cada prueba empieza con un usuario nuevo, su categoría de gastos y un comercio
    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoriaGasto = api.crearCategoria(token, idUsuario, "gasto");
        idComercio = api.crearComercio(api.login(api.registrarAdmin()), idCategoriaGasto);
    }

    private String gasto(int idGasto, String descripcion, String monto, LocalDate fecha) {
        return api.jsonGasto(idGasto, idUsuario, idCategoriaGasto, idComercio, descripcion, monto, fecha);
    }

    @Test
    @DisplayName("US11: registrar un gasto válido lo guarda y aparece en la lista del usuario")
    void us11_registrarGasto() throws Exception {
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gasto(0, "Almuerzo", "25.50", LocalDate.now())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idGasto").isNumber())
                .andExpect(jsonPath("$.montoGasto").value(25.50))
                .andExpect(jsonPath("$.idUsuario").value(idUsuario));

        mockMvc.perform(get("/gastos").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcionGasto").value("Almuerzo"));
    }

    @Test
    @DisplayName("US12: registrar un ingreso válido lo guarda con su categoría de ingreso")
    void us12_registrarIngreso() throws Exception {
        int idCategoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        String ingreso = """
                {"idUsuario":%d,"idCategoria":%d,"monto":1500,"fecha":"%s","tipoIngreso":"fijo",
                 "frecuencia":"mensual","fuente":"Sueldo","descripcion":"Pago de noviembre"}
                """.formatted(idUsuario, idCategoriaIngreso, LocalDate.now());
        mockMvc.perform(post("/ingresos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(ingreso))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(1500.0))
                .andExpect(jsonPath("$.fuente").value("Sueldo"));

        mockMvc.perform(get("/ingresos").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("US12: un ingreso no se puede registrar con una categoría de tipo gasto")
    void us12_ingresoConCategoriaDeGasto() throws Exception {
        String ingreso = """
                {"idUsuario":%d,"idCategoria":%d,"monto":100,"fecha":"%s","tipoIngreso":"fijo",
                 "frecuencia":"mensual","fuente":"Sueldo"}
                """.formatted(idUsuario, idCategoriaGasto, LocalDate.now());
        mockMvc.perform(post("/ingresos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(ingreso))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La categoría debe ser de tipo ingreso"));
    }

    @Test
    @DisplayName("US13: editar un gasto actualiza sus datos en la base de datos")
    void us13_editarGasto() throws Exception {
        int idGasto = api.crearGasto(token, gasto(0, "Taxi", "15", LocalDate.now()));

        mockMvc.perform(put("/gastos/actualiza").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gasto(idGasto, "Taxi al aeropuerto", "45.90", LocalDate.now())))
                .andExpect(status().isOk())
                .andExpect(content().string("Gasto actualizado correctamente"));

        // Se comprueba directamente en la base de datos (a través del servicio)
        var guardado = gastoService.listId(idGasto).orElseThrow();
        assertThat(guardado.getDescripcionGasto()).isEqualTo("Taxi al aeropuerto");
        assertThat(guardado.getMontoGasto()).isEqualByComparingTo("45.90");
    }

    @Test
    @DisplayName("US14: eliminar un gasto lo quita de la lista (borrado lógico, la fila sigue en la base)")
    void us14_eliminarGasto() throws Exception {
        int idGasto = api.crearGasto(token, gasto(0, "Cine", "30", LocalDate.now()));

        mockMvc.perform(delete("/gastos/" + idGasto).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Gasto eliminado correctamente"));

        mockMvc.perform(get("/gastos/" + idGasto).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/gastos").header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("US16: un gasto sin descripción o sin monto se rechaza con un mensaje claro")
    void us16_camposObligatorios() throws Exception {
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gasto(0, "", "20", LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La descripción es obligatoria"));

        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gasto(0, "Sin monto", "0", LocalDate.now())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El monto debe ser mayor que cero"));
    }

    @Test
    @DisplayName("US16: un gasto con fecha futura se rechaza")
    void us16_fechaFutura() throws Exception {
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gasto(0, "Viaje", "100", LocalDate.now().plusDays(3))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha del gasto no puede ser futura"));
    }

    @Test
    @DisplayName("US19: filtrar por rango de fechas devuelve solo los gastos de ese periodo")
    void us19_filtrarPorFechas() throws Exception {
        LocalDate hoy = LocalDate.now();
        api.crearGasto(token, gasto(0, "Gasto de este mes", "10", hoy));
        api.crearGasto(token, gasto(0, "Gasto de hace dos meses", "20", hoy.minusMonths(2)));

        mockMvc.perform(get("/gastos/usuario/" + idUsuario)
                        .param("fechaInicio", hoy.withDayOfMonth(1).toString())
                        .param("fechaFin", hoy.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcionGasto").value("Gasto de este mes"));
    }

    @Test
    @DisplayName("US19: un rango con la fecha de inicio después de la fecha fin se rechaza")
    void us19_rangoInvalido() throws Exception {
        mockMvc.perform(get("/gastos/usuario/" + idUsuario)
                        .param("fechaInicio", "2026-12-31")
                        .param("fechaFin", "2026-01-01")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("La fecha de inicio no puede ser posterior a la fecha de fin"));
    }
}
