package pe.edu.upc.walletix.servicesimplements;

import java.time.LocalDate;
import java.util.List;

// Reglas puras de la gamificación (sin base de datos, así se prueban con pruebas unitarias):
// - el NIVEL sale de los puntos (XP) acumulados
// - la RACHA son los días seguidos con actividad
public final class Niveles {
    private Niveles() {
    }

    // XP mínimo de cada nivel y su nombre (el frontend lo traduce: level.principiante, level.aprendiz...)
    private static final int[] XP_MINIMO = {0, 100, 250, 500, 1000, 2000, 4000};
    private static final String[] NOMBRES = {
            "principiante", "aprendiz", "constante", "ahorrador", "ahorrador_pro", "experto", "maestro"};

    public static final int XP_POR_PROGRESO_DIARIO = 20;

    public record Nivel(int numero, String nombre, int xpInicio, Integer xpSiguiente) {
    }

    public static Nivel nivelDe(int xp) {
        int indice = 0;
        for (int i = 0; i < XP_MINIMO.length; i++) {
            if (xp >= XP_MINIMO[i]) {
                indice = i;
            }
        }
        Integer siguiente = indice + 1 < XP_MINIMO.length ? XP_MINIMO[indice + 1] : null;
        return new Nivel(indice + 1, NOMBRES[indice], XP_MINIMO[indice], siguiente);
    }

    // Días seguidos con actividad, terminando hoy o ayer (si hoy aún no registró, la racha no se pierde).
    // fechas: días distintos con actividad, en cualquier orden.
    public static int racha(List<LocalDate> fechas, LocalDate hoy) {
        java.util.Set<LocalDate> dias = new java.util.HashSet<>(fechas);
        LocalDate dia = dias.contains(hoy) ? hoy : hoy.minusDays(1);
        int racha = 0;
        while (dias.contains(dia)) {
            racha++;
            dia = dia.minusDays(1);
        }
        return racha;
    }
}
