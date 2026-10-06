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

    // Borrado lógico: 1 = activo, 0 = eliminado. "default 1" para que las filas que ya existían queden activas
    @Column(name = "estadoRol", nullable = false, columnDefinition = "integer default 1")
    private int estadoRol = 1;

    public Rol() {
    }

    public Rol(int idRol, String rol, Usuario usuario, int estadoRol) {
        this.idRol = idRol;
        this.rol = rol;
        this.usuario = usuario;
        this.estadoRol = estadoRol;
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

    public int getEstadoRol() {
        return estadoRol;
    }

    public void setEstadoRol(int estadoRol) {
        this.estadoRol = estadoRol;
    }
}
