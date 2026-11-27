package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos de una categoría
@Schema(description = "Categoría de gastos o de ingresos")
public class CategoriaDTO {
    @Schema(description = "Id de la categoría (0 al crear)", example = "0")
    private int idCategoria;
    @Schema(description = "Dueño de la categoría", example = "7")
    private int idUsuario;
    @Schema(description = "Nombre (único por usuario y tipo)", example = "Mascotas")
    private String nombreCategoria;
    @Schema(description = "\"gasto\" o \"ingreso\"", example = "gasto")
    private String tipoCategoria;
    @Schema(description = "Nombre del ícono de Material Icons", example = "pets")
    private String urlIconoCategoria;
    @Schema(description = "Color en hexadecimal", example = "#22C55E")
    private String colorHexCategoria;
    @Schema(description = "true si la tienen todos los usuarios (solo ADMIN)", example = "false")
    private boolean predeterminadoCategoria;
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
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
