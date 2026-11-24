package pe.edu.upc.walletix.unitarias;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import pe.edu.upc.walletix.i18n.I18nConfig;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas UNITARIAS de la elección de idioma (i18n de los Web Services): sin levantar Spring
class I18nConfigTest {

    private final LocaleResolver resolver = new I18nConfig().localeResolver();

    @ParameterizedTest(name = "Accept-Language \"{0}\" -> {1}")
    @DisplayName("Elige es-419 o en-US según la cabecera Accept-Language")
    @CsvSource({
            "'', es-419",
            "en-US, en-US",
            "en, en-US",
            "en-GB, en-US",
            "es-419, es-419",
            "es-PE, es-419",
            "fr-FR, es-419",
            "'fr-FR,en;q=0.9', en-US",
            "'pt-BR,es;q=0.8,en;q=0.5', es-419"
    })
    void eligeIdioma(String cabecera, String esperado) {
        MockHttpServletRequest peticion = new MockHttpServletRequest();
        if (!cabecera.isEmpty()) {
            peticion.addHeader("Accept-Language", cabecera);
        }
        Locale idioma = resolver.resolveLocale(peticion);
        assertThat(idioma.toLanguageTag()).isEqualTo(esperado);
    }
}
