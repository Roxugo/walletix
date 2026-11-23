package pe.edu.upc.walletix;

import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas de seguridad de la API: autenticación (JWT), autorización por rol,
// acceso a datos de otros usuarios, contraseñas y CORS.
// Corren con H2 en memoria (src/test/resources/application.properties), no tocan PostgreSQL.
@SpringBootTest
@AutoConfigureMockMvc
class SeguridadTests {

    private static final String CONTRASENA = "Clave123";
    private static final AtomicInteger contador = new AtomicInteger(1);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    // El mismo secreto con el que la API firma los tokens
    @Value("${jwt.secret:iV9SZtXfUGQfFqSk+r9whz6nTbMBiaFSio0kxaBSycUlrEkkUmlYzsHXFbX3dp/9uZqpF2nFDnwmONI3qbn+8Q==}")
    private String secreto;

    // ---------- Ayudantes ----------

    // Registra un usuario nuevo (correo y teléfono únicos) y devuelve su correo
    private String registrarUsuario() throws Exception {
        int n = contador.getAndIncrement();
        String correo = "usuario" + n + "@prueba.com";
        String json = """
                {"nombreUsuario":"Usuario %d","correoUsuario":"%s","contrasenaUsuario":"%s",
                 "telefonoUsuario":%d,"fechaNacimientoUsuario":"2000-01-01","segmentoUsuario":"estudiante",
                 "saldoActualUsuario":100,"puntosGamificacionUsuario":0}
                """.formatted(n, correo, CONTRASENA, 900000000 + n);
        mockMvc.perform(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
        return correo;
    }

    private String login(String correo) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"" + CONTRASENA + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(resultado.getResponse().getContentAsString(), "$.token");
    }

    private int idDe(String correo) {
        return usuarioService.buscarPorCorreo(correo).orElseThrow().getIdUsuario();
    }

    private String crearAdmin() throws Exception {
        String correo = registrarUsuario();
        Usuario usuario = usuarioService.buscarPorCorreo(correo).orElseThrow();
        Rol rolAdmin = new Rol();
        rolAdmin.setRol("ADMIN");
        rolAdmin.setUsuario(usuario);
        rolService.insert(rolAdmin);
        return correo;
    }

    private String base64Url(String texto) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String parteBase64Url) {
        return new String(Base64.getUrlDecoder().decode(parteBase64Url), StandardCharsets.UTF_8);
    }

    // ---------- 1. Autenticación ----------

    @Test
    @DisplayName("Sin token, una ruta protegida responde 401")
    void sinToken_401() throws Exception {
        mockMvc.perform(get("/gastos")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Con un token inventado (basura) responde 401")
    void tokenBasura_401() throws Exception {
        mockMvc.perform(get("/gastos").header("Authorization", "Bearer esto.no.esuntoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Si se cambia el rol dentro del token a ADMIN, la firma ya no coincide: 401")
    void tokenAlterado_401() throws Exception {
        String token = login(registrarUsuario());
        String[] partes = token.split("\\.");
        String payload = decodificar(partes[1]);
        assertThat(payload).contains("USUARIO");
        // El atacante edita el payload pero no puede recalcular la firma sin el secreto
        String tokenFalso = partes[0] + "." + base64Url(payload.replace("USUARIO", "ADMIN")) + "." + partes[2];

        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenFalso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un token firmado con otra clave secreta responde 401")
    void tokenOtraClave_401() throws Exception {
        String correo = registrarUsuario();
        String tokenFalso = Jwts.builder()
                .subject(correo)
                .claims(Map.of("roles", "ADMIN"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Jwts.SIG.HS512.key().build()) // clave aleatoria, no la de la API
                .compact();

        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + tokenFalso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Un token vencido responde 401 aunque la firma sea correcta")
    void tokenVencido_401() throws Exception {
        String correo = registrarUsuario();
        String tokenVencido = Jwts.builder()
                .subject(correo)
                .claims(Map.of("roles", "USUARIO"))
                .issuedAt(new Date(System.currentTimeMillis() - 7_200_000))
                .expiration(new Date(System.currentTimeMillis() - 3_600_000)) // venció hace 1 hora
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto)))
                .compact();

        mockMvc.perform(get("/gastos").header("Authorization", "Bearer " + tokenVencido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login con contraseña incorrecta o correo inexistente: 401 con el mismo mensaje")
    void loginIncorrecto_401_mismoMensaje() throws Exception {
        String correo = registrarUsuario();
        String mensajeClaveMala = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"" + correo + "\",\"contrasena\":\"otraClave\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        String mensajeNoExiste = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"noexiste@prueba.com\",\"contrasena\":\"otraClave\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();
        // Mismo mensaje: un atacante no puede saber qué correos están registrados
        assertThat(mensajeNoExiste).isEqualTo(mensajeClaveMala);
    }

    @Test
    @DisplayName("Inyección SQL en el login no permite entrar")
    void inyeccionSqlEnLogin_401() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"' OR '1'='1\",\"contrasena\":\"' OR '1'='1\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- 2. Contraseñas ----------

    @Test
    @DisplayName("La contraseña se guarda cifrada con BCrypt y nunca se devuelve en el JSON")
    void contrasenaCifradaYOculta() throws Exception {
        String correo = registrarUsuario();
        Usuario guardado = usuarioService.buscarPorCorreo(correo).orElseThrow();
        assertThat(guardado.getContrasenaUsuario()).startsWith("$2").isNotEqualTo(CONTRASENA);

        mockMvc.perform(get("/usuarios/yo").header("Authorization", "Bearer " + login(correo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contrasenaUsuario").doesNotExist())
                .andExpect(content().string(not(containsString("$2"))));
    }

    @Test
    @DisplayName("El registro libre siempre da el rol USUARIO y 0 puntos (no se puede escalar privilegios)")
    void registroNoEscalaPrivilegios() throws Exception {
        int n = contador.getAndIncrement();
        String correo = "listo" + n + "@prueba.com";
        String json = """
                {"nombreUsuario":"Listo","correoUsuario":"%s","contrasenaUsuario":"%s","telefonoUsuario":%d,
                 "fechaNacimientoUsuario":"2000-01-01","segmentoUsuario":"estudiante","saldoActualUsuario":0,
                 "puntosGamificacionUsuario":99999,"roles":[{"rol":"ADMIN"}]}
                """.formatted(correo, CONTRASENA, 900000000 + n);
        mockMvc.perform(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puntosGamificacionUsuario").value(0));

        String payload = decodificar(login(correo).split("\\.")[1]);
        assertThat(payload).contains("\"roles\":\"USUARIO\"").doesNotContain("ADMIN");
    }

    // ---------- 3. Autorización por rol ----------

    @Test
    @DisplayName("Un USUARIO no puede listar todos los usuarios (solo ADMIN): 403")
    void usuarioNoListaUsuarios_403() throws Exception {
        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + login(registrarUsuario())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un ADMIN sí puede listar todos los usuarios: 200")
    void adminListaUsuarios_200() throws Exception {
        mockMvc.perform(get("/usuarios").header("Authorization", "Bearer " + login(crearAdmin())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Un USUARIO no puede crear comercios (solo ADMIN): 403")
    void usuarioNoCreaComercios_403() throws Exception {
        mockMvc.perform(post("/comercios/web").header("Authorization", "Bearer " + login(registrarUsuario()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCategoria\":1,\"nombreComerciante\":\"Hack\",\"urlLogoComerciante\":\"x\"}"))
                .andExpect(status().isForbidden());
    }

    // ---------- 4. Datos de otros usuarios (IDOR) ----------

    @Test
    @DisplayName("Un USUARIO no puede ver la cuenta de otro usuario cambiando el id: 403")
    void usuarioNoVeOtraCuenta_403() throws Exception {
        String tokenA = login(registrarUsuario());
        int idB = idDe(registrarUsuario());
        mockMvc.perform(get("/usuarios/" + idB).header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un USUARIO no puede registrar datos a nombre de otro usuario: 403")
    void usuarioNoCreaDatosDeOtro_403() throws Exception {
        String tokenA = login(registrarUsuario());
        int idB = idDe(registrarUsuario());
        String categoria = """
                {"idUsuario":%d,"nombreCategoria":"Ajena","tipoCategoria":"gasto",
                 "urlIconoCategoria":"https://icono.com/a.png","colorHexCategoria":"#FF5733"}
                """.formatted(idB);
        mockMvc.perform(post("/categorias/web").header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON).content(categoria))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Un USUARIO no puede eliminar la cuenta de otro usuario: 403")
    void usuarioNoEliminaOtraCuenta_403() throws Exception {
        String tokenA = login(registrarUsuario());
        String correoB = registrarUsuario();
        mockMvc.perform(delete("/usuarios/" + idDe(correoB)).header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isForbidden());
        assertThat(usuarioService.buscarPorCorreo(correoB)).isPresent();
    }

    @Test
    @DisplayName("GET /usuarios/yo devuelve solo la cuenta del dueño del token")
    void yoDevuelveSoloMiCuenta() throws Exception {
        String correo = registrarUsuario();
        mockMvc.perform(get("/usuarios/yo").header("Authorization", "Bearer " + login(correo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoUsuario").value(correo));
    }

    // ---------- 5. CORS ----------

    @Test
    @DisplayName("CORS: el frontend Angular (localhost:4200) sí puede llamar a la API")
    void corsOrigenPermitido() throws Exception {
        mockMvc.perform(options("/gastos")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    @DisplayName("CORS: una página de otro dominio no puede llamar a la API desde el navegador")
    void corsOrigenNoPermitido() throws Exception {
        mockMvc.perform(options("/gastos")
                        .header("Origin", "https://sitio-malicioso.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
