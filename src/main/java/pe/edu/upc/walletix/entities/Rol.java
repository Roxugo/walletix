package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Roles de seguridad del usuario (modelo del profe: un usuario tiene uno o varios roles)
@Entity
@Table(name = "Rol", uniqueConstraints = {@UniqueConstraint(columnNames = {"idUsuario", "rol"})})
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRol;

    // USUARIO o ADMIN
    @Column(name = "rol", length = 30, nullable = false)
    private String rol;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    public Rol() {
    }

    public Rol(int idRol, String rol, Usuario usuario) {
        this.idRol = idRol;
        this.rol = rol;
        this.usuario = usuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
