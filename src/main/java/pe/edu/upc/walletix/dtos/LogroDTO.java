package pe.edu.upc.walletix.dtos;



public class LogroDTO {
    private int idLogro;
    private String nombreLogro;
    private String descripcionLogro;
    private String urlIconoLogro;
    private int puntosLogro;

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
}
