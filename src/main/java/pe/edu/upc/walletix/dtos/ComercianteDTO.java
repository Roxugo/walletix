package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

// Datos de un comercio
@Schema(description = "Comercio donde se hacen los gastos")
public class ComercianteDTO {
    @Schema(description = "Id del comercio (0 al crear)", example = "0")
    private int idComerciante;
    @Schema(description = "Categoría del comercio (un gasto solo puede usar comercios de su categoría)", example = "1")
    private int idCategoria;
    @Schema(description = "Nombre del comercio", example = "Veterinaria Patitas")
    private String nombreComerciante;
    @Schema(description = "URL del logo (opcional)", example = "https://example.com/logo.png")
    private String urlLogoComerciante;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoComerciante = 1;
    // Dueño del comercio: null si es predeterminado (lo ven todos). Lo decide la API según quién lo crea.
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Dueño (null en los comercios predeterminados)", example = "7")
    private Integer idUsuario;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "true si lo tienen todos los usuarios", example = "false")
    private boolean predeterminadoComerciante;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isPredeterminadoComerciante() {
        return predeterminadoComerciante;
    }

    public void setPredeterminadoComerciante(boolean predeterminadoComerciante) {
        this.predeterminadoComerciante = predeterminadoComerciante;
    }

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
