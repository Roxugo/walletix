package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// US32: consejos y microlecciones que el usuario guardó como favoritos
@Entity
@Table(name = "FavoritoMicroleccion",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idUsuario", "idMicroleccion"}))
public class FavoritoMicroleccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFavoritoMicroleccion;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idMicroleccion", nullable = false)
    private Microleccion microleccion;

    public FavoritoMicroleccion() {
    }

    public FavoritoMicroleccion(Usuario usuario, Microleccion microleccion) {
        this.usuario = usuario;
        this.microleccion = microleccion;
    }

    public int getIdFavoritoMicroleccion() {
        return idFavoritoMicroleccion;
    }

    public void setIdFavoritoMicroleccion(int idFavoritoMicroleccion) {
        this.idFavoritoMicroleccion = idFavoritoMicroleccion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Microleccion getMicroleccion() {
        return microleccion;
    }

    public void setMicroleccion(Microleccion microleccion) {
        this.microleccion = microleccion;
    }
}
