package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos de un intento de quiz. aprobadoIntentoQuizUsuario lo calcula el sistema
@Schema(description = "Intento de quiz de un usuario")
public class IntentoQuizUsuarioDTO {
    @Schema(description = "Id del intento", example = "0")
    private int idIntentoQuizUsuario;
    @Schema(description = "Usuario", example = "7")
    private int idUsuario;
    @Schema(description = "Microlección", example = "1")
    private int idMicroleccion;
    @Schema(description = "Puntaje de 0 a 100", example = "100")
    private int puntajeIntentoQuizUsuario;
    @Schema(description = "true si llegó al puntaje aprobatorio", example = "true")
    private boolean aprobadoIntentoQuizUsuario;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoIntentoQuizUsuario = 1;

    public int getIdIntentoQuizUsuario() {
        return idIntentoQuizUsuario;
    }

    public void setIdIntentoQuizUsuario(int idIntentoQuizUsuario) {
        this.idIntentoQuizUsuario = idIntentoQuizUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdMicroleccion() {
        return idMicroleccion;
    }

    public void setIdMicroleccion(int idMicroleccion) {
        this.idMicroleccion = idMicroleccion;
    }

    public int getPuntajeIntentoQuizUsuario() {
        return puntajeIntentoQuizUsuario;
    }

    public void setPuntajeIntentoQuizUsuario(int puntajeIntentoQuizUsuario) {
        this.puntajeIntentoQuizUsuario = puntajeIntentoQuizUsuario;
    }

    public boolean isAprobadoIntentoQuizUsuario() {
        return aprobadoIntentoQuizUsuario;
    }

    public void setAprobadoIntentoQuizUsuario(boolean aprobadoIntentoQuizUsuario) {
        this.aprobadoIntentoQuizUsuario = aprobadoIntentoQuizUsuario;
    }

    public int getEstadoIntentoQuizUsuario() {
        return estadoIntentoQuizUsuario;
    }

    public void setEstadoIntentoQuizUsuario(int estadoIntentoQuizUsuario) {
        this.estadoIntentoQuizUsuario = estadoIntentoQuizUsuario;
    }
}
