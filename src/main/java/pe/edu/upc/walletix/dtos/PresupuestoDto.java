package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presupuesto mensual de una categoría")
public class PresupuestoDto {
    @Schema(description = "Id del presupuesto (0 al crear)", example = "0")
    private int idPresupuesto;
    @Schema(description = "Mes de 1 a 12", example = "11")
    private int mes;
    @Schema(description = "Año (del actual a 5 años más)", example = "2026")
    private int anio;
    @Schema(description = "Tope del mes en soles (hasta 1,000,000)", example = "500.00")
    private float montoAsignado;
    @Schema(description = "Dueño del presupuesto", example = "7")
    private int idUsuario;
    @Schema(description = "Categoría de tipo \"gasto\" (un presupuesto por categoría y mes)", example = "1")
    private int idCategoria;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estado = 1;

    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public float getMontoAsignado() {
        return montoAsignado;
    }

    public void setMontoAsignado(float montoAsignado) {
        this.montoAsignado = montoAsignado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
