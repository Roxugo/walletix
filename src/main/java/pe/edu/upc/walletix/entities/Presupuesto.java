package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "presupuesto")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPresupuesto;

    @Column(name = "mes", nullable = false)
    private int mes;

    @Column(name = "anio", nullable = false)
    private int anio;

    @Column(name = "montoAsignado", nullable = false)
    private float montoAsignado;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categoria categoria;

    public Presupuesto() {

    }

    public Presupuesto(int idPresupuesto, Usuario usuario, Categoria categoria, int mes, int anio, float montoAsignado) {
        this.idPresupuesto = idPresupuesto;
        this.usuario = usuario;
        this.categoria = categoria;
        this.mes = mes;
        this.anio = anio;
        this.montoAsignado = montoAsignado;
    }

    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public float getMontoAsignado() {
        return montoAsignado;
    }

    public void setMontoAsignado(float montoAsignado) {
        this.montoAsignado = montoAsignado;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

}

