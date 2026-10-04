package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(name = "nombreUsuario", length = 50, nullable = false)
    private String nombreUsuario;

    @Column(name = "correoUsuario", length = 50, nullable = false)
    private String correoUsuario;

    @Column(name = "telefonoUsuario", nullable = false)
    private int telefonoUsuario;

    @Column(name = "fechaNacimientoUsuario", nullable = false)
    private LocalDate fechaNacimientoUsuario;

    @Column(name = "segmentoUsuario", length = 30, nullable = false)
    private String segmentoUsuario;

    @Column(name = "saldoActualUsuario",precision = 10, scale = 2, nullable = false)
    private BigDecimal saldoActualUsuario;

    @Column(name = "puntosGamificacionUsuario", nullable = false)
    private int puntosGamificacionUsuario;

    @Column(name = "estadoUsuario", nullable = false)
    private int estadoUsuario = 1;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreUsuario, String correoUsuario, int telefonoUsuario, LocalDate fechaNacimientoUsuario, String segmentoUsuario, BigDecimal saldoActualUsuario, int puntosGamificacionUsuario, int estadoUsuario) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correoUsuario = correoUsuario;
        this.telefonoUsuario = telefonoUsuario;
        this.fechaNacimientoUsuario = fechaNacimientoUsuario;
        this.segmentoUsuario = segmentoUsuario;
        this.saldoActualUsuario = saldoActualUsuario;
        this.puntosGamificacionUsuario = puntosGamificacionUsuario;
        this.estadoUsuario = estadoUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public int getTelefonoUsuario() {
        return telefonoUsuario;
    }

    public void setTelefonoUsuario(int telefonoUsuario) {
        this.telefonoUsuario = telefonoUsuario;
    }

    public LocalDate getFechaNacimientoUsuario() {
        return fechaNacimientoUsuario;
    }

    public void setFechaNacimientoUsuario(LocalDate fechaNacimientoUsuario) {
        this.fechaNacimientoUsuario = fechaNacimientoUsuario;
    }

    public String getSegmentoUsuario() {
        return segmentoUsuario;
    }

    public void setSegmentoUsuario(String segmentoUsuario) {
        this.segmentoUsuario = segmentoUsuario;
    }

    public BigDecimal getSaldoActualUsuario() {
        return saldoActualUsuario;
    }

    public void setSaldoActualUsuario(BigDecimal saldoActualUsuario) {
        this.saldoActualUsuario = saldoActualUsuario;
    }

    public int getPuntosGamificacionUsuario() {
        return puntosGamificacionUsuario;
    }

    public void setPuntosGamificacionUsuario(int puntosGamificacionUsuario) {
        this.puntosGamificacionUsuario = puntosGamificacionUsuario;
    }

    public int getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(int estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }
}
