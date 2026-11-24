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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de INTEGRACIÓN de las categorías:
// - Las predeterminadas (las crea un ADMIN) las ven todos, pero un USUARIO no las edita ni elimina
// - Cada usuario crea sus propias categorías, que solo ve él
// - No se repiten nombres: un usuario no puede tener dos categorías con el mismo nombre y tipo
@SpringBootTest
@AutoConfigureMockMvc
class CategoriasIntegracionTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    private ApiPruebas api;
    private String tokenAdmin;
    private int idAdmin;
    private String token;
    private int idUsuario;

    @BeforeEach
    void preparar() throws Exception {
        api = new ApiPruebas(mockMvc, usuarioService, rolService);
        String correoAdmin = api.registrarAdmin();
        tokenAdmin = api.login(correoAdmin);
        idAdmin = api.idDe(correoAdmin);
        String correo = api.registrarUsuario();
        token = api.login(correo);
        idUsuario = api.idDe(correo);
    }

    // Nombre único por prueba (la base H2 se comparte entre clases de prueba)
    private String nombre(String base) {
        return base + " " + api.siguiente();
    }

    private String json(Integer idCategoria, int idDueno, String nombre, String tipo, boolean predeterminada) {
        return """
                {%s"idUsuario":%d,"nombreCategoria":"%s","tipoCategoria":"%s",
                 "urlIconoCategoria":"restaurant","colorHexCategoria":"#F97316","predeterminadoCategoria":%s}
                """.formatted(idCategoria == null ? "" : "\"idCategoria\":" + idCategoria + ",",
                idDueno, nombre, tipo, predeterminada);
    }

    private ResultActions crear(String tokenUsado, String cuerpo) throws Exception {
        return mockMvc.perform(post("/categorias/web").header("Authorization", "Bearer " + tokenUsado)
                .header("Accept-Language", "es-419")
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    private ResultActions actualizar(String tokenUsado, String cuerpo) throws Exception {
        return mockMvc.perform(put("/categorias/actualiza").header("Authorization", "Bearer " + tokenUsado)
                .header("Accept-Language", "es-419")
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    private int idCreado(ResultActions resultado) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), "$.idCategoria");
    }

    private List<String> nombresVisibles(String tokenUsado) throws Exception {
        String respuesta = mockMvc.perform(get("/categorias").header("Authorization", "Bearer " + tokenUsado))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$[*].nombreCategoria");
    }

    @Test
    @DisplayName("Una predeterminada la ven todos, pero un USUARIO no puede editarla ni eliminarla")
    void predeterminadaVisibleYProtegida() throws Exception {
        String nombre = nombre("Hogar");
        int id = idCreado(crear(tokenAdmin, json(null, idAdmin, nombre, "gasto", true)).andExpect(status().isCreated()));

        assertThat(nombresVisibles(token)).contains(nombre);
        actualizar(token, json(id, idAdmin, nombre + " editada", "gasto", true)).andExpect(status().isForbidden());
        mockMvc.perform(delete("/categorias/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("No se puede crear una categoría propia con el nombre de una predeterminada (sin importar mayúsculas ni espacios)")
    void nombreDePredeterminadaRepetido() throws Exception {
        String nombre = nombre("Mercado");
        crear(tokenAdmin, json(null, idAdmin, nombre, "gasto", true)).andExpect(status().isCreated());

        crear(token, json(null, idUsuario, "  " + nombre.toUpperCase() + " ", "gasto", false))
                .andExpect(status().isConflict())
                .andExpect(content().string("Ya tienes una categoría con ese nombre y tipo"));
    }

    @Test
    @DisplayName("El mismo nombre sí se permite si el tipo es distinto (gasto e ingreso)")
    void mismoNombreOtroTipo() throws Exception {
        String nombre = nombre("Ventas");
        crear(token, json(null, idUsuario, nombre, "gasto", false)).andExpect(status().isCreated());
        crear(token, json(null, idUsuario, nombre, "ingreso", false)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Las categorías propias son privadas: otro usuario puede usar el mismo nombre y no ve las ajenas")
    void categoriasPropiasPrivadas() throws Exception {
        String nombre = nombre("Mascotas");
        crear(token, json(null, idUsuario, nombre, "gasto", false)).andExpect(status().isCreated());
        crear(token, json(null, idUsuario, nombre, "gasto", false)).andExpect(status().isConflict());

        String correoOtro = api.registrarUsuario();
        String tokenOtro = api.login(correoOtro);
        assertThat(nombresVisibles(tokenOtro)).doesNotContain(nombre);
        crear(tokenOtro, json(null, api.idDe(correoOtro), nombre, "gasto", false)).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Al editar, guardar con su mismo nombre está bien; ponerle el nombre de otra categoría no")
    void editarSinDuplicar() throws Exception {
        String nombreA = nombre("Gimnasio");
        String nombreB = nombre("Cursos");
        int idA = idCreado(crear(token, json(null, idUsuario, nombreA, "gasto", false)).andExpect(status().isCreated()));
        crear(token, json(null, idUsuario, nombreB, "gasto", false)).andExpect(status().isCreated());

        actualizar(token, json(idA, idUsuario, nombreA, "gasto", false)).andExpect(status().isOk());
        actualizar(token, json(idA, idUsuario, nombreB, "gasto", false)).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Un USUARIO no puede crear categorías predeterminadas aunque lo pida")
    void usuarioNoCreaPredeterminadas() throws Exception {
        String respuesta = crear(token, json(null, idUsuario, nombre("Viajes"), "gasto", true))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        assertThat((Boolean) JsonPath.read(respuesta, "$.predeterminadoCategoria")).isFalse();
    }
}
