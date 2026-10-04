package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "desafio")
public class Desafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDesafio;

    @Column(name = "titulo", length = 30, nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 100, nullable = false)
    private String descripcion;

    @Column(name = "montoObjetivo", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoObjetivo;

    @Column(name = "puntosRecompensa", nullable = false)
    private int puntosRecompensa;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "saldoInicial", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldoInicial;

    @Column(name = "edadMinima", nullable = false)
    private int edadMinima;

    @Column(name = "saldoProyectado", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldoProyectado;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    @OneToMany(mappedBy = "desafio")
    private List<UsuarioDesafio> usuarioDesafios;

    public Desafio() {
    }

    public Desafio(int idDesafio, String titulo, String descripcion, BigDecimal montoObjetivo, int puntosRecompensa,
                   LocalDate fechaInicio, LocalDate fechaFin, BigDecimal saldoInicial, int edadMinima,
                   BigDecimal saldoProyectado, int estado) {
        this.idDesafio = idDesafio;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.montoObjetivo = montoObjetivo;
        this.puntosRecompensa = puntosRecompensa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.saldoInicial = saldoInicial;
        this.edadMinima = edadMinima;
        this.saldoProyectado = saldoProyectado;
        this.estado = estado;
    }

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

    public List<UsuarioDesafio> getUsuarioDesafios() {
        return usuarioDesafios;
    }

    public void setUsuarioDesafios(List<UsuarioDesafio> usuarioDesafios) {
        this.usuarioDesafios = usuarioDesafios;
    }
}