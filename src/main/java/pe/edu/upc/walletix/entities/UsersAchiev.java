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
    private Achievement achievement;

    public UsersAchiev() {
    }

    public UsersAchiev(int idUsersAchiev, Usuario usuario, Achievement achievement) {
        this.idUsersAchiev = idUsersAchiev;
        this.usuario = usuario;
        this.achievement = achievement;
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

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}
