package pe.edu.upc.walletix.dtos;

import java.util.List;
import java.util.Map;

// Respuestas de la sección "Aprende" (US31-US35): consejos, microlecciones, favoritos y quiz.
// Son "records": clases de solo lectura que Java arma con su constructor y sus getters.
public final class AprendeDTOs {
    private AprendeDTOs() {
    }

    // Tarjeta de la lista (US31, US32, US33). Si está bloqueada, "condicion" dice qué hacer para abrirla.
    public record ContenidoResumenDTO(
            int idMicroleccion,
            String titulo,
            String descripcion,
            String categoria,
            String tipo, // "leccion" o "tip"
            int minutosLectura,
            int preguntas,
            int puntosRecompensa,
            boolean bloqueado,
            String condicion,
            boolean favorito,
            boolean aprobado,
            Integer mejorPuntaje) {
    }

    // Una opción de respuesta: "a" + su texto
    public record OpcionDTO(String letra, String texto) {
    }

    // Pregunta SIN la respuesta correcta: la API califica, el navegador nunca la conoce antes de enviar
    public record PreguntaDTO(int idPregunta, String enunciado, List<OpcionDTO> opciones) {
    }

    // La microlección abierta (US34): su contenido y las preguntas de su quiz
    public record ContenidoDetalleDTO(
            ContenidoResumenDTO resumen,
            String contenido,
            String tituloQuiz,
            int puntajeAprobatorio,
            List<PreguntaDTO> preguntas) {
    }

    // Lo que envía el usuario: id de la pregunta -> letra elegida ("a", "b", "c" o "d")
    public record RespuestasQuizDTO(Map<Integer, String> respuestas) {
    }

    // Retroalimentación de una pregunta (US35)
    public record RetroalimentacionDTO(
            int idPregunta,
            String enunciado,
            String respuestaUsuario,
            String respuestaCorrecta,
            String textoCorrecto,
            boolean acerto,
            String explicacion) {
    }

    // Resultado del quiz (US35): puntaje, si aprobó, los puntos ganados y la retroalimentación
    public record ResultadoQuizDTO(
            int puntaje,
            int correctas,
            int total,
            int puntajeAprobatorio,
            boolean aprobado,
            boolean primeraVezAprobado,
            int puntosGanados,
            List<String> logrosDesbloqueados,
            List<RetroalimentacionDTO> retroalimentacion) {
    }

    // Respuesta al marcar o quitar un favorito (US32)
    public record FavoritoDTO(int idMicroleccion, boolean favorito, long totalFavoritos) {
    }
}
