package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Consejos (tip) y microlecciones con quiz (leccion) de educación financiera
@Entity
@Table(name = "Microleccion")
public class Microleccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMicroleccion;

    @Column(name = "tituloMicroleccion", length = 150, nullable = false)
    private String tituloMicroleccion;

    @Column(name = "descripcionMicroleccion", length = 255, nullable = false)
    private String descripcionMicroleccion;

    @Column(name = "contenidoMicroleccion", columnDefinition = "TEXT", nullable = false)
    private String contenidoMicroleccion;

    @Column(name = "urlMediaMicroleccion", length = 100, nullable = false)
    private String urlMediaMicroleccion;

    // Ahorro, Inversión, Deudas...
    @Column(name = "categoriaEducativaMicroleccion", length = 50, nullable = false)
    private String categoriaEducativaMicroleccion;

    // tip o leccion
    @Column(name = "tipoMicroleccion", length = 20, nullable = false)
    private String tipoMicroleccion;

    @Column(name = "bloqueadoMicroleccion", nullable = false)
    private boolean bloqueadoMicroleccion;

    @Column(name = "tituloQuizMicroleccion", length = 150, nullable = false)
    private String tituloQuizMicroleccion;

    // Puntaje mínimo (0 a 100) para aprobar el quiz
    @Column(name = "puntajeAprobatorioMicroleccion", nullable = false)
    private int puntajeAprobatorioMicroleccion;

    @Column(name = "puntosRecompensaMicroleccion", nullable = false)
    private int puntosRecompensaMicroleccion;

    // Borrado lógico: true = activo, false = eliminado
    @Column(name = "estadoMicroleccion", nullable = false)
    private boolean estadoMicroleccion = true;

    public Microleccion() {
    }

    public Microleccion(int idMicroleccion, String tituloMicroleccion, String descripcionMicroleccion, String contenidoMicroleccion, String urlMediaMicroleccion, String categoriaEducativaMicroleccion, String tipoMicroleccion, boolean bloqueadoMicroleccion, String tituloQuizMicroleccion, int puntajeAprobatorioMicroleccion, int puntosRecompensaMicroleccion, boolean estadoMicroleccion) {
        this.idMicroleccion = idMicroleccion;
        this.tituloMicroleccion = tituloMicroleccion;
        this.descripcionMicroleccion = descripcionMicroleccion;
        this.contenidoMicroleccion = contenidoMicroleccion;
        this.urlMediaMicroleccion = urlMediaMicroleccion;
        this.categoriaEducativaMicroleccion = categoriaEducativaMicroleccion;
        this.tipoMicroleccion = tipoMicroleccion;
        this.bloqueadoMicroleccion = bloqueadoMicroleccion;
        this.tituloQuizMicroleccion = tituloQuizMicroleccion;
        this.puntajeAprobatorioMicroleccion = puntajeAprobatorioMicroleccion;
        this.puntosRecompensaMicroleccion = puntosRecompensaMicroleccion;
        this.estadoMicroleccion = estadoMicroleccion;
    }

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

    public boolean isEstadoMicroleccion() {
        return estadoMicroleccion;
    }

    public void setEstadoMicroleccion(boolean estadoMicroleccion) {
        this.estadoMicroleccion = estadoMicroleccion;
    }
}
