package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos de un consejo o microlección
@Schema(description = "Microlección o tip de Aprende (la administra un ADMIN)")
public class MicroleccionDTO {
    @Schema(description = "Id (0 al crear)", example = "0")
    private int idMicroleccion;
    @Schema(description = "Título", example = "La regla 50/30/20")
    private String tituloMicroleccion;
    @Schema(description = "Resumen corto", example = "Una forma simple de repartir tu dinero cada mes.")
    private String descripcionMicroleccion;
    @Schema(description = "Contenido (una línea en blanco separa párrafos)", example = "Divide tu ingreso en tres partes: 50% necesidades, 30% gustos y 20% ahorro.")
    private String contenidoMicroleccion;
    @Schema(description = "Imagen o video opcional", example = "https://example.com/50-30-20.png")
    private String urlMediaMicroleccion;
    @Schema(description = "Categoría", example = "presupuesto")
    private String categoriaEducativaMicroleccion;
    @Schema(description = "\"leccion\" o \"tip\"", example = "leccion")
    private String tipoMicroleccion;
    @Schema(description = "true si se desbloquea al superar un reto oficial", example = "false")
    private boolean bloqueadoMicroleccion;
    @Schema(description = "Título del quiz", example = "Quiz: regla 50/30/20")
    private String tituloQuizMicroleccion;
    @Schema(description = "Puntaje mínimo para aprobar (0-100)", example = "70")
    private int puntajeAprobatorioMicroleccion;
    @Schema(description = "Puntos al aprobar por primera vez", example = "20")
    private int puntosRecompensaMicroleccion;
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
    private int estadoMicroleccion = 1;

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

    public int getEstadoMicroleccion() {
        return estadoMicroleccion;
    }

    public void setEstadoMicroleccion(int estadoMicroleccion) {
        this.estadoMicroleccion = estadoMicroleccion;
    }
}
