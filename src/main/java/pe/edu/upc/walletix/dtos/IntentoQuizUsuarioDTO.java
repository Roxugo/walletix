package pe.edu.upc.walletix.dtos;

// Datos de un intento de quiz. aprobadoIntentoQuizUsuario lo calcula el sistema
public class IntentoQuizUsuarioDTO {
    private int idIntentoQuizUsuario;
    private int idUsuario;
    private int idMicroleccion;
    private int puntajeIntentoQuizUsuario;
    private boolean aprobadoIntentoQuizUsuario;
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
