package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

// Cada vez que el usuario presiona "Registrar progreso diario" en un reto queda un registro con la fecha.
// Sirve para la RACHA (días seguidos con progreso) del Centro de Retos y Logros.
@Entity
@Table(name = "progresoDesafio")
public class ProgresoDesafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProgresoDesafio;

    @ManyToOne
    @JoinColumn(name = "idUsuarioDesafio", nullable = false)
    private UsuarioDesafio usuarioDesafio;

    @Column(name = "fechaProgreso", nullable = false)
    private LocalDate fechaProgreso;

    public ProgresoDesafio() {
    }

    public ProgresoDesafio(UsuarioDesafio usuarioDesafio, LocalDate fechaProgreso) {
        this.usuarioDesafio = usuarioDesafio;
        this.fechaProgreso = fechaProgreso;
    }

    public int getIdProgresoDesafio() {
        return idProgresoDesafio;
    }

    public UsuarioDesafio getUsuarioDesafio() {
        return usuarioDesafio;
    }

    public LocalDate getFechaProgreso() {
        return fechaProgreso;
    }
}
