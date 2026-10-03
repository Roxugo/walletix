package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Cada vez que un usuario rinde el quiz de una microlección
@Entity
@Table(name = "IntentoQuizUsuario")
public class IntentoQuizUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idIntentoQuizUsuario;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idMicroleccion", nullable = false)
    private Microleccion microleccion;

    // Puntaje de 0 a 100
    @Column(name = "puntajeIntentoQuizUsuario", nullable = false)
    private int puntajeIntentoQuizUsuario;

    // true si el puntaje es mayor o igual al puntaje aprobatorio de la microlección
    @Column(name = "aprobadoIntentoQuizUsuario", nullable = false)
    private boolean aprobadoIntentoQuizUsuario;

    // Borrado lógico: true = activo, false = eliminado
    @Column(name = "estadoIntentoQuizUsuario", nullable = false)
    private boolean estadoIntentoQuizUsuario = true;

    public IntentoQuizUsuario() {
    }

    public IntentoQuizUsuario(int idIntentoQuizUsuario, Usuario usuario, Microleccion microleccion, int puntajeIntentoQuizUsuario, boolean aprobadoIntentoQuizUsuario, boolean estadoIntentoQuizUsuario) {
        this.idIntentoQuizUsuario = idIntentoQuizUsuario;
        this.usuario = usuario;
        this.microleccion = microleccion;
        this.puntajeIntentoQuizUsuario = puntajeIntentoQuizUsuario;
        this.aprobadoIntentoQuizUsuario = aprobadoIntentoQuizUsuario;
        this.estadoIntentoQuizUsuario = estadoIntentoQuizUsuario;
    }

    public int getIdIntentoQuizUsuario() {
        return idIntentoQuizUsuario;
    }

    public void setIdIntentoQuizUsuario(int idIntentoQuizUsuario) {
        this.idIntentoQuizUsuario = idIntentoQuizUsuario;
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

    public int getPuntajeIntentoQuizUsuario() {
        return puntajeIntentoQuizUsuario;
    }

    public void setPuntajeIntentoQuizUsuario(int puntajeIntentoQuizUsuario) {
        this.puntajeIntentoQuizUsuario = puntajeIntentoQuizUsuario;
    }

    public boolean isAprobadoIntentoQuizUsuario() {
        return aprobadoIntentoQuizUsuario;
    }

    public void setAprobadoIntentoQuizUsuario(boolean aprobadoIntentoQuizUsuario) {
        this.aprobadoIntentoQuizUsuario = aprobadoIntentoQuizUsuario;
    }

    public boolean isEstadoIntentoQuizUsuario() {
        return estadoIntentoQuizUsuario;
    }

    public void setEstadoIntentoQuizUsuario(boolean estadoIntentoQuizUsuario) {
        this.estadoIntentoQuizUsuario = estadoIntentoQuizUsuario;
    }
}
