package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Reto oficial (lo crea un ADMIN)")
public class DesafioDTO {
    // Opcionales (Centro de Retos): días que hay que registrar progreso y tipo de reto.
    // Si no se envía la duración, son los días entre fechaInicio y fechaFin.
    @Schema(description = "Días que dura el reto", example = "7")
    private Integer duracionDias;
    @Schema(description = "\"sin_delivery\", \"sin_gastos_hormiga\", \"transporte_inteligente\", \"ahorro_libre\"…", example = "sin_delivery")
    private String tipoDesafio;

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getTipoDesafio() {
        return tipoDesafio;
    }

    public void setTipoDesafio(String tipoDesafio) {
        this.tipoDesafio = tipoDesafio;
    }

    @Schema(description = "Id del reto (0 al crear)", example = "0")

    private int idDesafio;
    @Schema(description = "Título del reto", example = "Semana sin delivery")
    private String titulo;
    @Schema(description = "Descripción", example = "Cocina en casa 7 días y guarda lo que gastarías en delivery")
    private String descripcion;
    @Schema(description = "Ahorro estimado en soles", example = "140.00")
    private BigDecimal montoObjetivo;
    @Schema(description = "Puntos al superarlo", example = "50")
    private int puntosRecompensa;
    @Schema(description = "Desde cuándo se puede unir", example = "2026-11-24")
    private LocalDate fechaInicio;
    @Schema(description = "Hasta cuándo está disponible", example = "2026-12-31")
    private LocalDate fechaFin;
    @Schema(description = "Saldo mínimo sugerido", example = "0.00")
    private BigDecimal saldoInicial;
    @Schema(description = "Edad mínima", example = "16")
    private int edadMinima;
    @Schema(description = "Saldo proyectado al terminar", example = "0.00")
    private BigDecimal saldoProyectado;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
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