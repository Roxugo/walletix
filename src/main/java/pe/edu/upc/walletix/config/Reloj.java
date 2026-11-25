package pe.edu.upc.walletix.config;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

// "Qué día es hoy" para el Centro de Retos (hora de Perú, ver ZonaHorariaConfig).
// Está en un componente aparte para que las pruebas puedan simular "mañana" y probar
// la regla de "un progreso por día" sin esperar 24 horas.
@Component
public class Reloj {
    public LocalDate hoy() {
        return LocalDate.now();
    }
}
