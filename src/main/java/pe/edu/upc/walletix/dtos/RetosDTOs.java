package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Respuestas del Centro de Retos y Logros (US26-US30).
// Son "records": clases de solo lectura que Java arma con su constructor y sus getters.
public final class RetosDTOs {
    private RetosDTOs() {
    }

    // Parte de arriba del Centro: nivel, efectividad, dinero blindado y racha + los retos del usuario
    public record ResumenRetosDTO(
            int nivel,
            String nombreNivel,          // clave del nivel: principiante, aprendiz, ahorrador...
            int xp,                      // puntos de gamificación del usuario
            int xpInicioNivel,
            Integer xpSiguienteNivel,    // null en el último nivel
            long retosSuperados,
            long retosTotales,
            BigDecimal dineroBlindado,   // suma de las metas de los retos superados
            int racha,                   // días seguidos registrando progreso
            List<ParticipacionDTO> activos,
            List<ParticipacionDTO> historial) {
    }

    // Un reto en el que participa el usuario ("Día 5 de 7")
    public record ParticipacionDTO(
            int idUsuarioDesafio,
            int idDesafio,
            String titulo,
            String descripcion,
            String tipo,
            boolean oficial,
            int diaActual,
            int duracionDias,
            BigDecimal porcentaje,
            BigDecimal ahorroEstimado,
            BigDecimal montoObjetivo,
            int puntosRecompensa,
            String estadoDesafio,        // EN_PROGRESO, COMPLETADO, VENCIDO, ABANDONADO
            boolean registradoHoy,
            LocalDate fechaFin,
            long diasRestantes) {
    }

    // Un reto oficial al que el usuario se puede unir (US27) y qué requisitos le faltan (US28)
    public record RetoDisponibleDTO(
            int idDesafio,
            String titulo,
            String descripcion,
            String tipo,
            BigDecimal montoObjetivo,
            int puntosRecompensa,
            int duracionDias,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            int edadMinima,
            int participantes,
            boolean puedeUnirse,
            List<String> requisitosFaltantes) {
    }

    // Respuesta de "Registrar progreso diario": "¡Progreso registrado! +20 XP · Día 6 de 7"
    public record ProgresoRegistradoDTO(
            int xpGanado,
            int diaActual,
            int duracionDias,
            boolean completado,
            int puntosRecompensa,
            List<String> logrosDesbloqueados) {
    }

    // Una medalla de la vitrina, con su avance (US30: las desbloqueadas se pueden compartir)
    public record MedallaDTO(
            int idLogro,
            String nombre,
            String descripcion,
            int puntos,
            boolean desbloqueada,
            Long avanceActual,           // null si su avance no se mide automáticamente
            Long avanceMeta) {
    }

    // Formulario "Crear nuevo reto" (US29): tipo, duración y meta de ahorro
    @Schema(description = "Reto personal que crea el usuario (US29)")
    public record RetoPersonalDTO(
            @Schema(description = "\"sin_delivery\", \"sin_gastos_hormiga\", \"transporte_inteligente\" o \"ahorro_libre\"", example = "sin_delivery") String tipo,
            @Schema(description = "Título opcional (máximo 30 caracteres)", example = "Mi semana sin delivery") String titulo,
            @Schema(description = "Duración de 1 a 90 días", example = "7") Integer duracionDias,
            @Schema(description = "Meta de ahorro en soles (máximo 2 decimales)", example = "140.00") BigDecimal montoObjetivo) {
    }
}
