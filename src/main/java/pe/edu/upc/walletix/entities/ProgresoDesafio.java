package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import pe.edu.upc.walletix.servicesimplements.Niveles;

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

    // XP que dio este registro: 20 el primero del día y 0 los demás (null en registros antiguos = 20)
    @Column(name = "xpOtorgado")
    private Integer xpOtorgado;

    public ProgresoDesafio() {
    }

    public ProgresoDesafio(UsuarioDesafio usuarioDesafio, LocalDate fechaProgreso, int xpOtorgado) {
        this.usuarioDesafio = usuarioDesafio;
        this.fechaProgreso = fechaProgreso;
        this.xpOtorgado = xpOtorgado;
    }

    public int xpOtorgadoEfectivo() {
        return xpOtorgado == null ? Niveles.XP_POR_PROGRESO_DIARIO : xpOtorgado;
    }

    public Integer getXpOtorgado() {
        return xpOtorgado;
    }

    public void setXpOtorgado(Integer xpOtorgado) {
        this.xpOtorgado = xpOtorgado;
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
