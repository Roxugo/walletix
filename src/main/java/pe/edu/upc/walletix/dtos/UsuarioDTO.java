package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UsuarioDTO {
    private int idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private int telefonoUsuario;
    private LocalDate fechaNacimientoUsuario;
    private String segmentoUsuario;
    private BigDecimal saldoActualUsuario;
    private int puntosGamificacionUsuario;
    private int estadoUsuario = 1;

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
