package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "notificacion")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotificacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "titulo", length = 30, nullable = false)
    private String titulo;

    @Column(name = "tipo", length = 40, nullable = false)
    private String tipo;

    @Column(name = "mensaje", length = 100, nullable = false)
    private String mensaje;

    @Column(name = "estadoNotificacion", nullable = false)
    private boolean estadoNotificacion;

    @Column(name = "estado", nullable = false)
    private int estado = 1;

    public Notificacion() {
    }

    public Notificacion(int idNotificacion, Usuario usuario, String titulo, String tipo, String mensaje, boolean estadoNotificacion, int estado) {
        this.idNotificacion = idNotificacion;
        this.usuario = usuario;
        this.titulo = titulo;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.estadoNotificacion = estadoNotificacion;
        this.estado = estado;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isEstadoNotificacion() {
        return estadoNotificacion;
    }

    public void setEstadoNotificacion(boolean estadoNotificacion) {
        this.estadoNotificacion = estadoNotificacion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}