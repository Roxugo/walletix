package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "usuarioDesafio")
public class UsuarioDesafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuarioDesafio;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idDesafio", nullable = false)
    private Desafio desafio;

    @Column(name = "saldoInicial", precision = 12, scale = 2, nullable = false)
    private BigDecimal saldoInicial;

    @Column(name = "montoProgresoActual", precision = 12, scale = 2, nullable = false)
    private BigDecimal montoProgresoActual;

    @Column(name = "porcentajeProgreso", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeProgreso;

    @Column(name = "estadoDesafio", length = 20, nullable = false)
    private String estadoDesafio;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    public UsuarioDesafio() {
    }

    public UsuarioDesafio(int idUsuarioDesafio, Usuario usuario, Desafio desafio, BigDecimal saldoInicial,
                          BigDecimal montoProgresoActual, BigDecimal porcentajeProgreso,
                          String estadoDesafio, int estado) {
        this.idUsuarioDesafio = idUsuarioDesafio;
        this.usuario = usuario;
        this.desafio = desafio;
        this.saldoInicial = saldoInicial;
        this.montoProgresoActual = montoProgresoActual;
        this.porcentajeProgreso = porcentajeProgreso;
        this.estadoDesafio = estadoDesafio;
        this.estado = estado;
    }

    public int getIdUsuarioDesafio() {
        return idUsuarioDesafio;
    }

    public void setIdUsuarioDesafio(int idUsuarioDesafio) {
        this.idUsuarioDesafio = idUsuarioDesafio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Desafio getDesafio() {
        return desafio;
    }

    public void setDesafio(Desafio desafio) {
        this.desafio = desafio;
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