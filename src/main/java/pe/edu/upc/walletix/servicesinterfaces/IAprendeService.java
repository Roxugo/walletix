package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.dtos.AprendeDTOs.*;
import pe.edu.upc.walletix.entities.Usuario;

import java.util.List;

// Sección "Aprende" (US31-US35): consejos por categoría, favoritos, contenido bloqueado, microlecciones y quiz
public interface IAprendeService {
    // US31 + US32 + US33: lista filtrada por categoría, por texto o solo favoritos (null = sin filtro)
    List<ContenidoResumenDTO> listar(Usuario usuario, String categoria, String buscar, boolean soloFavoritos);

    // US34: abrir una microlección o consejo. Si está bloqueado responde 403 con la condición para abrirlo (US33)
    ContenidoDetalleDTO abrir(Usuario usuario, int idMicroleccion);

    // US35: la API califica las respuestas, guarda el intento y da los puntos solo la primera vez que se aprueba
    ResultadoQuizDTO responderQuiz(Usuario usuario, int idMicroleccion, RespuestasQuizDTO respuestas);

    // US32: marca o quita el favorito (cada llamada lo invierte)
    FavoritoDTO alternarFavorito(Usuario usuario, int idMicroleccion);

    // Microlecciones: "tip de 30 segundos" del día (el mismo consejo para todo el día), o null si no hay consejos
    ContenidoDetalleDTO tipDelDia(Usuario usuario);

    // Error de una regla, con el código HTTP que debe responder la API
    class ReglaAprendeException extends RuntimeException {
        private final int status;

        public ReglaAprendeException(int status, String mensaje) {
            super(mensaje);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}
