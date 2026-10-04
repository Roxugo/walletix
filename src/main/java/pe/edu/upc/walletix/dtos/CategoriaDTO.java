package pe.edu.upc.walletix.dtos;

// Datos de una categoría
public class CategoriaDTO {
    private int idCategoria;
    private int idUsuario;
    private String nombreCategoria;
    private String tipoCategoria;
    private String urlIconoCategoria;
    private String colorHexCategoria;
    private boolean predeterminadoCategoria;
    private int estadoCategoria = 1;

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public String getUrlIconoCategoria() {
        return urlIconoCategoria;
    }

    public void setUrlIconoCategoria(String urlIconoCategoria) {
        this.urlIconoCategoria = urlIconoCategoria;
    }

    public String getColorHexCategoria() {
        return colorHexCategoria;
    }

    public void setColorHexCategoria(String colorHexCategoria) {
        this.colorHexCategoria = colorHexCategoria;
    }

    public boolean isPredeterminadoCategoria() {
        return predeterminadoCategoria;
    }

    public void setPredeterminadoCategoria(boolean predeterminadoCategoria) {
        this.predeterminadoCategoria = predeterminadoCategoria;
    }

    public int getEstadoCategoria() {
        return estadoCategoria;
    }

    public void setEstadoCategoria(int estadoCategoria) {
        this.estadoCategoria = estadoCategoria;
    }
}
