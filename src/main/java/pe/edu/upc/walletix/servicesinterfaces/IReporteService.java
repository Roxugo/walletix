package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Usuario;

import java.time.YearMonth;

// US39: reporte financiero del usuario en PDF
public interface IReporteService {
    // PDF con el resumen, los meses, los gastos por categoría y los movimientos del periodo.
    // Si no hay movimientos responde 404 con el mensaje "no hay información" (escenario 2 de la US39).
    byte[] reportePdf(Usuario usuario, YearMonth desde, YearMonth hasta);

    // Error de una regla, con el código HTTP que debe responder la API
    class ReglaReporteException extends RuntimeException {
        private final int status;

        public ReglaReporteException(int status, String mensaje) {
            super(mensaje);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
