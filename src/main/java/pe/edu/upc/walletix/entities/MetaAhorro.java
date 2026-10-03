package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "metasAhorro")
public class MetaAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMetaAhorro;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "montoObjetivo", nullable = false)
    private BigDecimal montoObjetivo;

    @Column(name = "montoActual", nullable = false)
    private BigDecimal montoActual = BigDecimal.ZERO;

    @Column(name = "fechaLimite", nullable = false)
    private LocalDate fechaLimite;

    @Column(name = "estadoMetaAhorro", length = 15, nullable = false)
    private String estadoMetaAhorro;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idAuditoria")
    private Auditoria auditoria;

    public MetaAhorro (){

    }

    public MetaAhorro (int idMetaAhorro, Usuario usuario, Auditoria auditoria, String titulo, BigDecimal montoObjetivo, BigDecimal montoActual, LocalDate fechaLimite, String estadoMetaAhorro, int estado)
    {
        this.idMetaAhorro = idMetaAhorro;
        this.usuario = usuario;
        this.auditoria = auditoria;
        this.titulo = titulo;
        this.montoObjetivo = montoObjetivo;
        this.montoActual = montoActual;
        this.fechaLimite = fechaLimite;
        this.estadoMetaAhorro = estadoMetaAhorro;
        this.estado = estado;
    }

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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Auditoria getAuditoria() {
        return auditoria;
    }

    public void setAuditoria(Auditoria auditoria) {
        this.auditoria = auditoria;
    }
}