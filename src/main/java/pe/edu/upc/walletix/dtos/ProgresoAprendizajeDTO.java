package pe.edu.upc.walletix.dtos;

// Resultado del query nativo: progreso del usuario en cada microlección
public class ProgresoAprendizajeDTO {
    private int idMicroleccion;
    private String tituloMicroleccion;
    private int cantidadIntentos;
    private int mejorPuntaje;
    private boolean aprobado;

    public int getIdMicroleccion() {
        return idMicroleccion;
    }

    public void setIdMicroleccion(int idMicroleccion) {
        this.idMicroleccion = idMicroleccion;
    }

    public String getTituloMicroleccion() {
        return tituloMicroleccion;
    }

    public void setTituloMicroleccion(String tituloMicroleccion) {
        this.tituloMicroleccion = tituloMicroleccion;
    }

    public int getCantidadIntentos() {
        return cantidadIntentos;
    }

    public void setCantidadIntentos(int cantidadIntentos) {
        this.cantidadIntentos = cantidadIntentos;
    }

    public int getMejorPuntaje() {
        return mejorPuntaje;
    }

    public void setMejorPuntaje(int mejorPuntaje) {
        this.mejorPuntaje = mejorPuntaje;
    }

    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }
}
