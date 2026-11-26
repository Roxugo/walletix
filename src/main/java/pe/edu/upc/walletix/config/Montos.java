package pe.edu.upc.walletix.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

// Formato de los montos en los mensajes de la API: separador de miles y 2 decimales ("2,390.00"),
// el mismo que muestra la app en es-419 y en-US
public final class Montos {
    private Montos() {
    }

    public static String formato(BigDecimal monto) {
        BigDecimal valor = monto == null ? BigDecimal.ZERO : monto.setScale(2, RoundingMode.HALF_UP);
        return String.format(Locale.US, "%,.2f", valor);
    }
}
