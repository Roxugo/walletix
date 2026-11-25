package pe.edu.upc.walletix.servicesimplements;

import pe.edu.upc.walletix.entities.Gasto;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

// Verificación de los retos con los GASTOS que el usuario registra en Walletix.
// Un gasto "rompe" el reto del día en que se hizo. Solo se verifica lo que se registra en la app
// (sin conexión con el banco ni con las apps de delivery: eso queda como mejora futura).
public final class ReglasReto {

    // Gasto hormiga: compra pequeña del día a día (snack, café, golosinas)
    public static final BigDecimal LIMITE_GASTO_HORMIGA = new BigDecimal("10");

    private static final List<String> DELIVERY = List.of(
            "delivery", "rappi", "pedidosya", "pedidos ya", "didi food", "uber eats", "glovo");
    // Transporte inteligente = bus, metro, bicicleta o a pie: rompe el reto un taxi o una app de taxi
    private static final List<String> TAXI = List.of("taxi", "uber", "cabify", "indriver", "indrive", "didi");

    private ReglasReto() {
    }

    // "sin_compras_impulsivas" y "ahorro_libre" no se pueden detectar con datos: siguen siendo por honestidad
    public static boolean seVerificaConGastos(String tipo) {
        return "sin_delivery".equals(tipo) || "sin_gastos_hormiga".equals(tipo) || "transporte_inteligente".equals(tipo);
    }

    public static boolean rompe(String tipo, Gasto gasto) {
        if (tipo == null || gasto == null || gasto.getEstadoGasto() == null || gasto.getEstadoGasto() != 1) {
            return false;
        }
        String texto = texto(gasto);
        return switch (tipo) {
            case "sin_delivery" -> contiene(texto, DELIVERY);
            // Los gastos fijos (suscripciones) y el pasaje del transporte no son gastos hormiga
            case "sin_gastos_hormiga" -> gasto.getMontoGasto() != null
                    && gasto.getMontoGasto().compareTo(LIMITE_GASTO_HORMIGA) < 0
                    && !gasto.isFijoGasto()
                    && !normalizar(gasto.getCategoria() == null ? null : gasto.getCategoria().getNombreCategoria()).contains("transporte");
            // "Uber Eats" o "DiDi Food" son delivery, no taxi
            case "transporte_inteligente" -> contiene(texto, TAXI) && !contiene(texto, DELIVERY);
            default -> false;
        };
    }

    // El primer gasto de la lista que rompe el reto (para avisarle al usuario cuál fue)
    public static Optional<Gasto> primeroQueRompe(String tipo, List<Gasto> gastos) {
        return gastos.stream().filter(gasto -> rompe(tipo, gasto)).findFirst();
    }

    // Comercio + descripción + categoría, en minúsculas y sin tildes
    private static String texto(Gasto gasto) {
        return normalizar((gasto.getComerciante() == null ? "" : gasto.getComerciante().getNombreComerciante()) + " "
                + gasto.getDescripcionGasto() + " "
                + (gasto.getCategoria() == null ? "" : gasto.getCategoria().getNombreCategoria()));
    }

    private static boolean contiene(String texto, List<String> palabras) {
        return palabras.stream().anyMatch(texto::contains);
    }

    static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().replaceAll("\\s+", " ");
    }
}
