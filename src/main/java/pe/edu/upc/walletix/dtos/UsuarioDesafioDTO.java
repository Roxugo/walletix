package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Participación de un usuario en un reto")
public class UsuarioDesafioDTO {
    @Schema(description = "Id de la participación", example = "0")
    private int idUsuarioDesafio;
    @Schema(description = "Usuario", example = "7")
    private int idUsuario;
    @Schema(description = "Reto", example = "6")
    private int idDesafio;
    @Schema(description = "Saldo al unirse", example = "1500.00")
    private BigDecimal saldoInicial;
    @Schema(description = "Ahorro acumulado", example = "20.00")
    private BigDecimal montoProgresoActual;
    @Schema(description = "Avance en porcentaje", example = "14.29")
    private BigDecimal porcentajeProgreso;
    @Schema(description = "\"EN_PROGRESO\", \"COMPLETADO\", \"ABANDONADO\" o \"VENCIDO\"", example = "EN_PROGRESO")
    private String estadoDesafio;
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
    private int estado = 1;

    public int getIdUsuarioDesafio() {
        return idUsuarioDesafio;
    }

    public void setIdUsuarioDesafio(int idUsuarioDesafio) {
        this.idUsuarioDesafio = idUsuarioDesafio;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdDesafio() {
        return idDesafio;
    }

    public void setIdDesafio(int idDesafio) {
        this.idDesafio = idDesafio;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getMontoProgresoActual() {
        return montoProgresoActual;
    }

    public void setMontoProgresoActual(BigDecimal montoProgresoActual) {
        this.montoProgresoActual = montoProgresoActual;
    }

    public BigDecimal getPorcentajeProgreso() {
        return porcentajeProgreso;
    }

    public void setPorcentajeProgreso(BigDecimal porcentajeProgreso) {
        this.porcentajeProgreso = porcentajeProgreso;
    }

    public String getEstadoDesafio() {
        return estadoDesafio;
    }

    public void setEstadoDesafio(String estadoDesafio) {
        this.estadoDesafio = estadoDesafio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}