package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// Gastos registrados por el usuario
@Entity
@Table(name = "Gasto")
public class Gasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGasto;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "idComerciante", nullable = false)
    private Comerciante comerciante;

    @Column(name = "descripcionGasto", length = 100, nullable = false)
    private String descripcionGasto;

    @Column(name = "montoGasto", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoGasto;

    @Column(name = "fechaGasto", nullable = false)
    private LocalDate fechaGasto;

    // Efectivo, Yape, Tarjeta...
    @Column(name = "metodoPagoGasto", length = 20, nullable = false)
    private String metodoPagoGasto;

    // true = gasto fijo (alquiler, pensión...)
    @Column(name = "fijoGasto", nullable = false)
    private boolean fijoGasto;

    // Borrado lógico: 1 = activo, 0 = eliminado
    @Column(name = "estadoGasto", nullable = false)
    private Integer estadoGasto = 1;

    public Gasto() {
    }

    public Gasto(int idGasto, Usuario usuario, Categoria categoria, Comerciante comerciante, String descripcionGasto, BigDecimal montoGasto, LocalDate fechaGasto, String metodoPagoGasto, boolean fijoGasto, Integer estadoGasto) {
        this.idGasto = idGasto;
        this.usuario = usuario;
        this.categoria = categoria;
        this.comerciante = comerciante;
        this.descripcionGasto = descripcionGasto;
        this.montoGasto = montoGasto;
        this.fechaGasto = fechaGasto;
        this.metodoPagoGasto = metodoPagoGasto;
        this.fijoGasto = fijoGasto;
        this.estadoGasto = estadoGasto;
    }

    public int getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(int idGasto) {
        this.idGasto = idGasto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Comerciante getComerciante() {
        return comerciante;
    }

    public void setComerciante(Comerciante comerciante) {
        this.comerciante = comerciante;
    }

    public String getDescripcionGasto() {
        return descripcionGasto;
    }

    public void setDescripcionGasto(String descripcionGasto) {
        this.descripcionGasto = descripcionGasto;
    }

    public BigDecimal getMontoGasto() {
        return montoGasto;
    }

    public void setMontoGasto(BigDecimal montoGasto) {
        this.montoGasto = montoGasto;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getMetodoPagoGasto() {
        return metodoPagoGasto;
    }

    public void setMetodoPagoGasto(String metodoPagoGasto) {
        this.metodoPagoGasto = metodoPagoGasto;
    }

    public boolean isFijoGasto() {
        return fijoGasto;
    }

    public void setFijoGasto(boolean fijoGasto) {
        this.fijoGasto = fijoGasto;
    }

    public Integer getEstadoGasto() {
        return estadoGasto;
    }

    public void setEstadoGasto(Integer estadoGasto) {
        this.estadoGasto = estadoGasto;
    }
}
