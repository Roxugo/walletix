package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DesafioDTO {
    private int idDesafio;
    private String titulo;
    private String descripcion;
    private BigDecimal montoObjetivo;
    private int puntosRecompensa;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal saldoInicial;
    private int edadMinima;
    private BigDecimal saldoProyectado;
    private int estado = 1;

    public int getIdDesafio() {
        return idDesafio;
    }

    public void setIdDesafio(int idDesafio) {
        this.idDesafio = idDesafio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMontoObjetivo() {
        return montoObjetivo;
    }

    public void setMontoObjetivo(BigDecimal montoObjetivo) {
        this.montoObjetivo = montoObjetivo;
    }

    public int getPuntosRecompensa() {
        return puntosRecompensa;
    }

    public void setPuntosRecompensa(int puntosRecompensa) {
        this.puntosRecompensa = puntosRecompensa;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public BigDecimal getSaldoProyectado() {
        return saldoProyectado;
    }

    public void setSaldoProyectado(BigDecimal saldoProyectado) {
        this.saldoProyectado = saldoProyectado;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}