package pe.edu.upc.walletix.soporte;

import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Ayudante de las pruebas de integración y de aceptación: prepara los datos llamando a la API real
// (MockMvc + base H2 en memoria), igual que lo haría la aplicación web.
// No es un @Component para que Spring no intente crearlo en pruebas que no tienen MockMvc.
public class ApiPruebas {

    public static final String CONTRASENA = "Clave123";
    // Empieza en 5000 para no repetir correos ni teléfonos de otras clases de prueba
    private static final AtomicInteger contador = new AtomicInteger(5000);

    private final MockMvc mockMvc;
    private final IUsuarioService usuarioService;
    private final IRolService rolService;

    public ApiPruebas(MockMvc mockMvc, IUsuarioService usuarioService, IRolService rolService) {
        this.mockMvc = mockMvc;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    public MockMvc mvc() {
        return mockMvc;
    }

    public int siguiente() {
        return contador.getAndIncrement();
    }

    // JSON de registro de un usuario válido
    public String jsonUsuario(String correo, int numero) {
        return """
                {"nombreUsuario":"Usuario %d","correoUsuario":"%s","contrasenaUsuario":"%s",
                 "telefonoUsuario":%d,"fechaNacimientoUsuario":"2000-01-01","segmentoUsuario":"estudiante",
                 "saldoActualUsuario":1000,"puntosGamificacionUsuario":0}
                """.formatted(numero, correo, CONTRASENA, 920000000 + numero);
    }

    public ResultActions registrar(String json) throws Exception {
        return mockMvc.perform(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    // Registra un usuario nuevo y devuelve su correo
    public String registrarUsuario() throws Exception {
        int n = siguiente();
        String correo = "prueba" + n + "@walletix.test";
        registrar(jsonUsuario(correo, n)).andExpect(status().isCreated());
        return correo;
    }

    public ResultActions intentarLogin(String correo, String contrasena) throws Exception {
        return mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"" + contrasena + "\"}"));
    }

    // Inicia sesión y devuelve el token JWT
    public String login(String correo) throws Exception {
        MvcResult resultado = intentarLogin(correo, CONTRASENA).andExpect(status().isOk()).andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    public int idDe(String correo) {
        return usuarioService.buscarPorCorreo(correo).orElseThrow().getIdUsuario();
    }

    // Registra un usuario y le agrega el rol ADMIN (para crear comercios)
    public String registrarAdmin() throws Exception {
        String correo = registrarUsuario();
        Usuario usuario = usuarioService.buscarPorCorreo(correo).orElseThrow();
        Rol rolAdmin = new Rol();
        rolAdmin.setRol("ADMIN");
        rolAdmin.setUsuario(usuario);
        rolService.insert(rolAdmin);
        return correo;
    }

    private int crear(String token, String ruta, String json, String campoId) throws Exception {
        MvcResult resultado = mockMvc.perform(post(ruta).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$." + campoId);
    }

    // Categoría propia del usuario; tipo = "gasto" o "ingreso"
    public int crearCategoria(String token, int idUsuario, String tipo) throws Exception {
        String json = """
                {"idUsuario":%d,"nombreCategoria":"Categoria %d","tipoCategoria":"%s",
                 "urlIconoCategoria":"https://iconos.test/a.png","colorHexCategoria":"#22C55E"}
                """.formatted(idUsuario, siguiente(), tipo);
        return crear(token, "/categorias/web", json, "idCategoria");
    }

    // Los comercios solo los crea un ADMIN
    public int crearComercio(String tokenAdmin, int idCategoria) throws Exception {
        String json = """
                {"idCategoria":%d,"nombreComerciante":"Comercio %d","urlLogoComerciante":"https://logos.test/c.png"}
                """.formatted(idCategoria, siguiente());
        return crear(tokenAdmin, "/comercios/web", json, "idComerciante");
    }

    public String jsonGasto(int idGasto, int idUsuario, int idCategoria, int idComercio,
                            String descripcion, String monto, LocalDate fecha) {
        return """
                {"idGasto":%d,"idUsuario":%d,"idCategoria":%d,"idComerciante":%d,"descripcionGasto":"%s",
                 "montoGasto":%s,"fechaGasto":"%s","metodoPagoGasto":"tarjeta","fijoGasto":false}
                """.formatted(idGasto, idUsuario, idCategoria, idComercio, descripcion, monto, fecha);
    }

    public int crearGasto(String token, String json) throws Exception {
        return crear(token, "/gastos/web", json, "idGasto");
    }

    public int crearIngreso(String token, int idUsuario, int idCategoria, String monto) throws Exception {
        String json = """
                {"idUsuario":%d,"idCategoria":%d,"monto":%s,"fecha":"%s","tipoIngreso":"fijo",
                 "frecuencia":"mensual","fuente":"Sueldo"}
                """.formatted(idUsuario, idCategoria, monto, LocalDate.now());
        return crear(token, "/ingresos/web", json, "idIngreso");
    }

    // Presupuesto del mes actual para una categoría de gastos
    public int crearPresupuesto(String token, int idUsuario, int idCategoria, String monto) throws Exception {
        LocalDate hoy = LocalDate.now();
        String json = """
                {"mes":%d,"anio":%d,"montoAsignado":%s,"idUsuario":%d,"idCategoria":%d}
                """.formatted(hoy.getMonthValue(), hoy.getYear(), monto, idUsuario, idCategoria);
        return crear(token, "/presupuestos/web", json, "idPresupuesto");
    }

    // Saldo actual del usuario del token (GET /usuarios/yo)
    public BigDecimal saldo(String token) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/usuarios/yo").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn();
        Object saldo = JsonPath.read(resultado.getResponse().getContentAsString(), "$.saldoActualUsuario");
        return new BigDecimal(saldo.toString());
    }
}
