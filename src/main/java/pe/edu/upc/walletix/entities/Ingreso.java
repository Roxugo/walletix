package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "ingreso")
public class Ingreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIngreso")
    private int idIngreso;

    @Column (name = "monto", nullable = false)
    private float monto;

    @Column (name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column (name = "tipoIngreso", length = 100, nullable = false)
    private String tipoIngreso;

    @Column (name = "frecuencia", nullable = false)
    private String frecuencia;

    @Column (name = "fuente", length = 100, nullable = false)
    private String fuente;

    @Column (name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    @ManyToOne
    @JoinColumn (name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn (name = "idCategoria")
    private Categoria categoria;

    public int getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(int idIngreso) {
        this.idIngreso = idIngreso;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEstado() { return estado; }

    public void setEstado(int estado) { this.estado = estado; }

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
