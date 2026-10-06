package pe.edu.upc.walletix.dtos;

// Datos de un comercio
public class ComercianteDTO {
    private int idComerciante;
    private int idCategoria;
    private String nombreComerciante;
    private String urlLogoComerciante;
    private int estadoComerciante = 1;

    public int getIdComerciante() {
        return idComerciante;
    }

    public void setIdComerciante(int idComerciante) {
        this.idComerciante = idComerciante;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreComerciante() {
        return nombreComerciante;
    }

    public void setNombreComerciante(String nombreComerciante) {
        this.nombreComerciante = nombreComerciante;
    }

    public String getUrlLogoComerciante() {
        return urlLogoComerciante;
    }

    public void setUrlLogoComerciante(String urlLogoComerciante) {
        this.urlLogoComerciante = urlLogoComerciante;
    }

    public int getEstadoComerciante() {
        return estadoComerciante;
    }

    public void setEstadoComerciante(int estadoComerciante) {
        this.estadoComerciante = estadoComerciante;
    }
}
