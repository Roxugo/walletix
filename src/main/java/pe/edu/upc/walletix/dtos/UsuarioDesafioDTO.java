package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;

public class UsuarioDesafioDTO {
    private int idUsuarioDesafio;
    private int idUsuario;
    private int idDesafio;
    private BigDecimal saldoInicial;
    private BigDecimal montoProgresoActual;
    private BigDecimal porcentajeProgreso;
    private String estadoDesafio;
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