package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Ingreso del usuario: se suma al saldo")
public class IngresoDto {
    @Schema(description = "Id del ingreso (0 al crear)", example = "0")
    private int idIngreso;
    @Schema(description = "Monto en soles, mayor que 0 y con máximo 2 decimales", example = "1500.00")
    private float monto;
    @Schema(description = "Fecha del ingreso (no futura)", example = "2026-11-25")
    private LocalDate fecha;
    @Schema(description = "\"fijo\" o \"variable\"", example = "fijo")
    private String tipoIngreso;
    @Schema(description = "\"unica\", \"semanal\", \"quincenal\" o \"mensual\"", example = "mensual")
    private String frecuencia;
    @Schema(description = "De dónde viene el ingreso", example = "Sueldo de practicante")
    private String fuente;
    @Schema(description = "Descripción opcional", example = "Pago de noviembre")
    private String descripcion;
    @Schema(description = "Dueño del ingreso", example = "7")
    private int idUsuario;
    @Schema(description = "Categoría de tipo \"ingreso\"", example = "5")
    private int idCategoria;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estado = 1;

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
