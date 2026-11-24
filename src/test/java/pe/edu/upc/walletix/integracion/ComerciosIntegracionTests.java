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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de INTEGRACIÓN de los comercios (US21), mismo patrón que las categorías:
// - Predeterminados (los crea un ADMIN, sin dueño): los ven todos, un cliente no los edita ni elimina
// - Propios (los crea un cliente): solo los ve y usa su dueño
// - Sin nombres repetidos entre lo que ve cada usuario
@SpringBootTest
@AutoConfigureMockMvc
class ComerciosIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    private ApiPruebas api;
    private String tokenAdmin;
    private String token;
    private int idUsuario;
    private int idCategoria; // categoría de gasto propia del cliente

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        tokenAdmin = api.login(api.registrarAdmin());
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
        idCategoria = api.crearCategoria(token, idUsuario, "gasto");
    }

    // Nombre único por prueba (la base H2 se comparte entre clases de prueba)
    private String nombre(String base) {
        return base + " " + api.siguiente();
    }

    private String json(Integer idComerciante, int idCategoriaUsada, String nombre) {
        return """
                {%s"idCategoria":%d,"nombreComerciante":"%s"}
                """.formatted(idComerciante == null ? "" : "\"idComerciante\":" + idComerciante + ",", idCategoriaUsada, nombre);
    }

    private ResultActions crear(String tokenUsado, String cuerpo) throws Exception {
        return mockMvc.perform(post("/comercios/web").header("Authorization", "Bearer " + tokenUsado)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    private ResultActions actualizar(String tokenUsado, String cuerpo) throws Exception {
        return mockMvc.perform(put("/comercios/actualiza").header("Authorization", "Bearer " + tokenUsado)
                .header("Accept-Language", "es-419").contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    private int idCreado(ResultActions resultado) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.idComerciante");
    }

    private List<String> nombresVisibles(String tokenUsado) throws Exception {
        String respuesta = mockMvc.perform(get("/comercios").header("Authorization", "Bearer " + tokenUsado))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$[*].nombreComerciante");
    }

    // Un comercio predeterminado (creado por el ADMIN) en una categoría de gasto
    private int comercioPredeterminado(String nombreComercio) throws Exception {
        return idCreado(crear(tokenAdmin, json(null, idCategoria, nombreComercio)).andExpect(status().isCreated()));
    }

    @Test
    @DisplayName("Un cliente crea su propio comercio: queda a su nombre, no es predeterminado y tiene un logo por defecto")
    void clienteCreaSuComercio() throws Exception {
        String nombre = nombre("Veterinaria");
        crear(token, json(null, idCategoria, nombre))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(idUsuario))
                .andExpect(jsonPath("$.predeterminadoComerciante").value(false))
                .andExpect(jsonPath("$.urlLogoComerciante").value("storefront"));
        assertThat(nombresVisibles(token)).contains(nombre);
    }

    @Test
    @DisplayName("Un comercio del ADMIN es predeterminado: lo ven todos, pero un cliente no lo edita ni elimina")
    void predeterminadoVisibleYProtegido() throws Exception {
        String nombre = nombre("Plaza Vea");
        int id = comercioPredeterminado(nombre);

        assertThat(nombresVisibles(token)).contains(nombre);
        actualizar(token, json(id, idCategoria, nombre + " editado")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/comercios/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        actualizar(tokenAdmin, json(id, idCategoria, nombre + " editado")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Los comercios propios son privados: otro cliente no los ve, no los abre y puede usar el mismo nombre")
    void comerciosPropiosPrivados() throws Exception {
        String nombre = nombre("Panadería");
        int id = idCreado(crear(token, json(null, idCategoria, nombre)).andExpect(status().isCreated()));

        String correoOtro = api.registrarUsuario();
        String tokenOtro = api.login(correoOtro);
        assertThat(nombresVisibles(tokenOtro)).doesNotContain(nombre);
        mockMvc.perform(get("/comercios/" + id).header("Authorization", "Bearer " + tokenOtro))
                .andExpect(status().isForbidden());
        int categoriaOtro = api.crearCategoria(tokenOtro, api.idDe(correoOtro), "gasto");
        crear(tokenOtro, json(null, categoriaOtro, nombre)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("No se repiten nombres: ni el de un predeterminado ni uno propio (sin importar mayúsculas ni espacios)")
    void sinNombresRepetidos() throws Exception {
        String predeterminado = nombre("Tottus");
        comercioPredeterminado(predeterminado);
        crear(token, json(null, idCategoria, " " + predeterminado.toUpperCase() + " "))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya tienes un comercio con ese nombre"));

        String propio = nombre("Gimnasio Barrio");
        int id = idCreado(crear(token, json(null, idCategoria, propio)).andExpect(status().isCreated()));
        crear(token, json(null, idCategoria, propio)).andExpect(status().isConflict());
        // Editarlo con su mismo nombre sí está bien
        actualizar(token, json(id, idCategoria, propio)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un cliente no puede poner su comercio en una categoría ajena ni en una de ingresos")
    void categoriaDelComercio() throws Exception {
        String correoOtro = api.registrarUsuario();
        int categoriaAjena = api.crearCategoria(api.login(correoOtro), api.idDe(correoOtro), "gasto");
        crear(token, json(null, categoriaAjena, nombre("Ajeno"))).andExpect(status().isBadRequest());

        int categoriaIngreso = api.crearCategoria(token, idUsuario, "ingreso");
        crear(token, json(null, categoriaIngreso, nombre("De ingreso"))).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Un gasto solo puede usar comercios predeterminados o propios, no el comercio privado de otro cliente")
    void gastoConComercioAjeno() throws Exception {
        String correoOtro = api.registrarUsuario();
        String tokenOtro = api.login(correoOtro);
        int categoriaOtro = api.crearCategoria(tokenOtro, api.idDe(correoOtro), "gasto");
        int comercioAjeno = idCreado(crear(tokenOtro, json(null, categoriaOtro, nombre("Privado"))).andExpect(status().isCreated()));
        int comercioPropio = idCreado(crear(token, json(null, idCategoria, nombre("Mío"))).andExpect(status().isCreated()));

        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(0, idUsuario, idCategoria, comercioAjeno, "Compra", "10", LocalDate.now())))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/gastos/web").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(api.jsonGasto(0, idUsuario, idCategoria, comercioPropio, "Compra", "10", LocalDate.now())))
                .andExpect(status().isCreated());
    }
}
