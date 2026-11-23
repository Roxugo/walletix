package pe.edu.upc.walletix.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

// Devuelve un mensaje en el idioma de la petición actual.
// Uso en un controller: mensajes.get("usuario.no.encontrado")  ->  "Usuario no encontrado" / "User not found"
// Con datos: mensajes.get("rol.ya.asignado", "ADMIN")  ->  reemplaza {0} por ADMIN
@Component
public class Mensajes {

    @Autowired
    private MessageSource messageSource;

    // Idioma de la petición actual (lo fija Spring con el LocaleResolver de I18nConfig)
    public String get(String clave, Object... datos) {
        return get(LocaleContextHolder.getLocale(), clave, datos);
    }

    // Para los filtros de seguridad, que se ejecutan antes de que Spring fije el idioma
    public String get(Locale idioma, String clave, Object... datos) {
        return messageSource.getMessage(clave, datos, idioma);
    }
}
