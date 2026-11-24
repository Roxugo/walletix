package pe.edu.upc.walletix.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

// Walletix es para usuarios del Perú: la API trabaja con la hora de Lima (UTC-5).
// Sin esto, en Render la API usa UTC y, desde las 7 p. m. de Perú, ya cree que es "mañana":
// las notificaciones saldrían con 5 horas de más y las validaciones de fechas usarían el día equivocado.
@Configuration
public class ZonaHorariaConfig {
    public static final String ZONA_PERU = "America/Lima";

    @PostConstruct
    public void usarHoraDePeru() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZONA_PERU));
    }
}
