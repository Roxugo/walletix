package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Logro")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLogro;

    @Column(name = "nombreLogro",length =50 ,nullable =false )
    private String nombreLogro;

    @Column(name = "descripcionLogro", length = 100, nullable = false)
    private String descripcionLogro;

    @Column(name = "urlIconoLogro", length = 100, nullable = false)
    private String urlIconoLogro;

    @Column(name = "puntosLogro", nullable = false)
    private int puntosLogro;

    @Column(name = "estadoLogro", nullable = false)
    private boolean estadoLogro = true;

    public Logro() {
    }

    public Logro(int idLogro, String nombreLogro, String descripcionLogro, String urlIconoLogro, int puntosLogro, boolean estadoLogro) {
        this.idLogro = idLogro;
        this.nombreLogro = nombreLogro;
        this.descripcionLogro = descripcionLogro;
        this.urlIconoLogro = urlIconoLogro;
        this.puntosLogro = puntosLogro;
        this.estadoLogro = estadoLogro;
    }

    public int getIdLogro() {
        return idLogro;
    }

    public void setIdLogro(int idLogro) {
        this.idLogro = idLogro;
    }

    public String getNombreLogro() {
        return nombreLogro;
    }

    public void setNombreLogro(String nombreLogro) {
        this.nombreLogro = nombreLogro;
    }

    public String getDescripcionLogro() {
        return descripcionLogro;
    }

    public void setDescripcionLogro(String descripcionLogro) {
        this.descripcionLogro = descripcionLogro;
    }

    public String getUrlIconoLogro() {
        return urlIconoLogro;
    }

    public void setUrlIconoLogro(String urlIconoLogro) {
        this.urlIconoLogro = urlIconoLogro;
    }

    public int getPuntosLogro() {
        return puntosLogro;
    }

    public void setPuntosLogro(int puntosLogro) {
        this.puntosLogro = puntosLogro;
    }

    public boolean isEstadoLogro() {
        return estadoLogro;
    }

    public void setEstadoLogro(boolean estadoLogro) {
        this.estadoLogro = estadoLogro;
    }
}
