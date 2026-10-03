package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "UsuarioLogro")
public class UsuarioLogro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuarioLogro;

    @Column(name = "estadoUsuarioLogro", nullable = false)
    private boolean estadoUsuarioLogro = true;

    @ManyToOne
    @JoinColumn(name = "IdUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "IdLogro")
    private Logro logro;

    public UsuarioLogro() {
    }

    public UsuarioLogro(int idUsuarioLogro, boolean estadoUsuarioLogro, Usuario usuario, Logro logro) {
        this.idUsuarioLogro = idUsuarioLogro;
        this.estadoUsuarioLogro = estadoUsuarioLogro;
        this.usuario = usuario;
        this.logro = logro;
    }

    public int getIdUsuarioLogro() {
        return idUsuarioLogro;
    }

    public void setIdUsuarioLogro(int idUsuarioLogro) {
        this.idUsuarioLogro = idUsuarioLogro;
    }

    public boolean isEstadoUsuarioLogro() {
        return estadoUsuarioLogro;
    }

    public void setEstadoUsuarioLogro(boolean estadoUsuarioLogro) {
        this.estadoUsuarioLogro = estadoUsuarioLogro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Logro getLogro() {
        return logro;
    }

    public void setLogro(Logro logro) {
        this.logro = logro;
    }
}
