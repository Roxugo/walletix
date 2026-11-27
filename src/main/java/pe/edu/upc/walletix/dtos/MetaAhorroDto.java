package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Meta de ahorro del usuario")
public class MetaAhorroDto {
    @Schema(description = "Id de la meta (0 al crear)", example = "0")
    private int idMetaAhorro;
    @Schema(description = "Nombre de la meta", example = "Viaje a Cusco")
    private String titulo;
    @Schema(description = "Monto a juntar en soles", example = "1800.00")
    private BigDecimal montoObjetivo;
    @Schema(description = "Monto ahorrado (no supera el objetivo; al llegar, la meta pasa a \"cumplida\")", example = "650.00")
    private BigDecimal montoActual;
    @Schema(description = "Fecha límite (al crear, no puede estar en el pasado)", example = "2027-04-30")
    private LocalDate fechaLimite;
    @Schema(description = "\"activa\", \"cumplida\" o \"pausada\"", example = "activa")
    private String estadoMetaAhorro;
    @Schema(description = "Dueño de la meta", example = "7")
    private int idUsuario;
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
    private int estado = 1;

    public int getIdMetaAhorro() {
        return idMetaAhorro;
    }

    public void setIdMetaAhorro(int idMetaAhorro) {
        this.idMetaAhorro = idMetaAhorro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getMontoObjetivo() {
        return montoObjetivo;
    }

    public void setMontoObjetivo(BigDecimal montoObjetivo) {
        this.montoObjetivo = montoObjetivo;
    }

    public BigDecimal getMontoActual() {
        return montoActual;
    }

    public void setMontoActual(BigDecimal montoActual) {
        this.montoActual = montoActual;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getEstadoMetaAhorro() {
        return estadoMetaAhorro;
    }

    public void setEstadoMetaAhorro(String estadoMetaAhorro) {
        this.estadoMetaAhorro = estadoMetaAhorro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
