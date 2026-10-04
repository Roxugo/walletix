package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MetaAhorroDto {
    private int idMetaAhorro;
    private String titulo;
    private BigDecimal montoObjetivo;
    private BigDecimal montoActual;
    private LocalDate fechaLimite;
    private String estadoMetaAhorro;
    private int idUsuario;
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
