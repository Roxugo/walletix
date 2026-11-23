package pe.edu.upc.walletix.i18n;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Collections;
import java.util.Locale;

// Internacionalización (i18n) de los Web Services: es-419 (español latinoamericano) y en-US (inglés)
// El cliente elige el idioma con la cabecera HTTP "Accept-Language" (la app Angular la envía sola)
@Configuration
public class I18nConfig {

    public static final Locale ESPANOL = Locale.forLanguageTag("es-419");
    public static final Locale INGLES = Locale.forLanguageTag("en-US");

    // Lee la cabecera Accept-Language (ej. "en-US,en;q=0.9,es;q=0.8") y elige uno de los dos idiomas:
    // el primero de la lista que sea inglés ("en", "en-US", "en-GB") o español ("es", "es-PE", "es-419").
    // Si no envían la cabecera o piden otro idioma (ej. francés), responde en español (es-419)
    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                if (request.getHeader("Accept-Language") == null) {
                    return ESPANOL;
                }
                for (Locale pedido : Collections.list(request.getLocales())) { // ya vienen ordenados por preferencia (q)
                    if (pedido.getLanguage().equals("en")) return INGLES;
                    if (pedido.getLanguage().equals("es")) return ESPANOL;
                }
                return ESPANOL;
            }
        };
    }

    // Archivos con los textos: src/main/resources/i18n/messages_es_419.properties y messages_en_US.properties
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource fuente = new ResourceBundleMessageSource();
        fuente.setBasenames("i18n/messages");
        fuente.setDefaultEncoding("UTF-8");          // para que las tildes y la ñ se lean bien
        fuente.setFallbackToSystemLocale(false);     // no usar el idioma del servidor, sino es-419
        fuente.setDefaultLocale(ESPANOL);
        return fuente;
    }
}
