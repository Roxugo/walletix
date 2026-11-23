package pe.edu.upc.walletix.securities;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import pe.edu.upc.walletix.i18n.Mensajes;

import java.io.IOException;

// Clase 7: responde 401 cuando se pide un recurso protegido sin token válido
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    private Mensajes mensajes;
    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Los filtros de seguridad corren antes que Spring MVC, por eso el idioma se lee aquí de la petición
        String mensaje = mensajes.get(localeResolver.resolveLocale(request), "no.autorizado.token");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + mensaje + "\"}");
    }
}
