package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "UsersAchiev")
public class UsersAchiev {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsersAchiev;

    @ManyToOne
    @JoinColumn(name = "IdUsers")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "IdAchievement")
    private Logro logro;

    public UsersAchiev() {
    }

    public UsersAchiev(int idUsersAchiev, Usuario usuario, Logro logro) {
        this.idUsersAchiev = idUsersAchiev;
        this.usuario = usuario;
        this.logro = logro;
    }

    public int getIdUsersAchiev() {
        return idUsersAchiev;
    }

    public void setIdUsersAchiev(int idUsersAchiev) {
        this.idUsersAchiev = idUsersAchiev;
    }

    public Usuario getUsers() {
        return usuario;
    }

    public void setUsers(Usuario usuario) {
        this.usuario = usuario;
    }

    public Logro getAchievement() {
        return logro;
    }

    public void setAchievement(Logro logro) {
        this.logro = logro;
    }
}
