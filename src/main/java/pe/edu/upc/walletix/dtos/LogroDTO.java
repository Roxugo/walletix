package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Logro que se desbloquea automáticamente")
public class LogroDTO {
    @Schema(description = "Id del logro (0 al crear)", example = "0")
    private int idLogro;
    @Schema(description = "Nombre del logro", example = "Primer gasto registrado")
    private String nombreLogro;
    @Schema(description = "Cómo se consigue", example = "Registraste tu primer gasto")
    private String descripcionLogro;
    @Schema(description = "Ícono", example = "receipt_long")
    private String urlIconoLogro;
    @Schema(description = "Puntos que da", example = "10")
    private int puntosLogro;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoLogro = 1;

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

    public int getEstadoLogro() {
        return estadoLogro;
    }

    public void setEstadoLogro(int estadoLogro) {
        this.estadoLogro = estadoLogro;
    }
}
