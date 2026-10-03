package pe.edu.upc.walletix.dtos;

// Datos de un consejo o microlección
public class MicroleccionDTO {
    private int idMicroleccion;
    private String tituloMicroleccion;
    private String descripcionMicroleccion;
    private String contenidoMicroleccion;
    private String urlMediaMicroleccion;
    private String categoriaEducativaMicroleccion;
    private String tipoMicroleccion;
    private boolean bloqueadoMicroleccion;
    private String tituloQuizMicroleccion;
    private int puntajeAprobatorioMicroleccion;
    private int puntosRecompensaMicroleccion;

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

    public String getDescripcionMicroleccion() {
        return descripcionMicroleccion;
    }

    public void setDescripcionMicroleccion(String descripcionMicroleccion) {
        this.descripcionMicroleccion = descripcionMicroleccion;
    }

    public String getContenidoMicroleccion() {
        return contenidoMicroleccion;
    }

    public void setContenidoMicroleccion(String contenidoMicroleccion) {
        this.contenidoMicroleccion = contenidoMicroleccion;
    }

    public String getUrlMediaMicroleccion() {
        return urlMediaMicroleccion;
    }

    public void setUrlMediaMicroleccion(String urlMediaMicroleccion) {
        this.urlMediaMicroleccion = urlMediaMicroleccion;
    }

    public String getCategoriaEducativaMicroleccion() {
        return categoriaEducativaMicroleccion;
    }

    public void setCategoriaEducativaMicroleccion(String categoriaEducativaMicroleccion) {
        this.categoriaEducativaMicroleccion = categoriaEducativaMicroleccion;
    }

    public String getTipoMicroleccion() {
        return tipoMicroleccion;
    }

    public void setTipoMicroleccion(String tipoMicroleccion) {
        this.tipoMicroleccion = tipoMicroleccion;
    }

    public boolean isBloqueadoMicroleccion() {
        return bloqueadoMicroleccion;
    }

    public void setBloqueadoMicroleccion(boolean bloqueadoMicroleccion) {
        this.bloqueadoMicroleccion = bloqueadoMicroleccion;
    }

    public String getTituloQuizMicroleccion() {
        return tituloQuizMicroleccion;
    }

    public void setTituloQuizMicroleccion(String tituloQuizMicroleccion) {
        this.tituloQuizMicroleccion = tituloQuizMicroleccion;
    }

    public int getPuntajeAprobatorioMicroleccion() {
        return puntajeAprobatorioMicroleccion;
    }

    public void setPuntajeAprobatorioMicroleccion(int puntajeAprobatorioMicroleccion) {
        this.puntajeAprobatorioMicroleccion = puntajeAprobatorioMicroleccion;
    }

    public int getPuntosRecompensaMicroleccion() {
        return puntosRecompensaMicroleccion;
    }

    public void setPuntosRecompensaMicroleccion(int puntosRecompensaMicroleccion) {
        this.puntosRecompensaMicroleccion = puntosRecompensaMicroleccion;
    }
}
