package pe.edu.upc.walletix;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// i18n de los Web Services: el idioma de los mensajes se elige con la cabecera Accept-Language
// es-419 (por defecto) y en-US
@SpringBootTest
@AutoConfigureMockMvc
class InternacionalizacionTests {

    @Autowired
    private MockMvc mockMvc;

    private static final String LOGIN_MALO = "{\"correo\":\"noexiste@prueba.com\",\"contrasena\":\"otraClave\"}";

    private Properties cargar(String archivo) throws IOException {
        Properties propiedades = new Properties();
        try (InputStream entrada = getClass().getResourceAsStream("/i18n/" + archivo)) {
            propiedades.load(new java.io.InputStreamReader(entrada, java.nio.charset.StandardCharsets.UTF_8));
        }
        return propiedades;
    }

    @Test
    @DisplayName("Sin cabecera Accept-Language responde en español (es-419)")
    void sinCabecera_espanol() throws Exception {
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Correo o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Con Accept-Language: en-US responde en inglés")
    void enUS_ingles() throws Exception {
        mockMvc.perform(post("/login").header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Incorrect email or password"));
    }

    @Test
    @DisplayName("Con Accept-Language: es-419 responde en español")
    void es419_espanol() throws Exception {
        mockMvc.perform(post("/login").header("Accept-Language", "es-419")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(content().string("Correo o contraseña incorrectos"));
    }

    @Test
    @DisplayName("Solo el idioma ('en', 'es-PE') también funciona; un idioma no soportado usa español")
    void idiomaParecido() throws Exception {
        mockMvc.perform(post("/login").header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(content().string("Incorrect email or password"));
        mockMvc.perform(post("/login").header("Accept-Language", "es-PE")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(content().string("Correo o contraseña incorrectos"));
        mockMvc.perform(post("/login").header("Accept-Language", "fr-FR")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(content().string("Correo o contraseña incorrectos"));
        // Como lo envía un navegador en inglés: varios idiomas con preferencia (q)
        mockMvc.perform(post("/login").header("Accept-Language", "fr-FR,en;q=0.9,es;q=0.8")
                        .contentType(MediaType.APPLICATION_JSON).content(LOGIN_MALO))
                .andExpect(content().string("Incorrect email or password"));
    }

    @Test
    @DisplayName("Las validaciones también se traducen (registro con teléfono inválido)")
    void validacionTraducida() throws Exception {
        String usuario = """
                {"nombreUsuario":"Test","correoUsuario":"i18n@prueba.com","contrasenaUsuario":"Clave123",
                 "telefonoUsuario":123,"fechaNacimientoUsuario":"2000-01-01","segmentoUsuario":"estudiante",
                 "saldoActualUsuario":0}
                """;
        mockMvc.perform(post("/usuarios/web").header("Accept-Language", "en-US")
                        .contentType(MediaType.APPLICATION_JSON).content(usuario))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The phone number must have exactly 9 digits"));
        mockMvc.perform(post("/usuarios/web").contentType(MediaType.APPLICATION_JSON).content(usuario))
                .andExpect(content().string("El teléfono debe tener exactamente 9 dígitos"));
    }

    @Test
    @DisplayName("El 401 sin token (filtro de seguridad) también respeta el idioma")
    void error401Traducido() throws Exception {
        mockMvc.perform(get("/gastos").header("Accept-Language", "en-US"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized: log in and send your token"));
        mockMvc.perform(get("/gastos"))
                .andExpect(jsonPath("$.error").value("No autorizado: inicie sesión y envíe su token"));
    }

    @Test
    @DisplayName("Los errores del GlobalExceptionHandler con datos se traducen ({0})")
    void errorConDatosTraducido() throws Exception {
        mockMvc.perform(delete("/login").header("Accept-Language", "en-US"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.error").value("Method DELETE is not allowed on this route"));
        mockMvc.perform(delete("/login"))
                .andExpect(jsonPath("$.error").value("Método DELETE no permitido en esta ruta"));
    }

    @Test
    @DisplayName("Los dos archivos de mensajes tienen exactamente las mismas claves")
    void mismasClaves() throws IOException {
        Properties espanol = cargar("messages_es_419.properties");
        Properties ingles = cargar("messages_en_US.properties");
        assertThat(espanol).isNotEmpty();
        assertThat(ingles.keySet()).isEqualTo(espanol.keySet());
    }
}
