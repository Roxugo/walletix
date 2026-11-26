package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Usuario")
// Solo actualiza las columnas modificadas (no pisa el saldo o los puntos que cambió otra petición)
@org.hibernate.annotations.DynamicUpdate
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    @Column(name = "nombreUsuario", length = 50, nullable = false)
    private String nombreUsuario;

    @Column(name = "correoUsuario", length = 50, nullable = false, unique = true)
    private String correoUsuario;

    @Column(name = "telefonoUsuario", nullable = false, unique = true)
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

    // Se guarda cifrada con BCrypt; el correo funciona como usuario para el login
    @Column(name = "contrasenaUsuario", length = 200)
    private String contrasenaUsuario;

    // US25: qué notificaciones automáticas quiere recibir (null = sí; así los usuarios existentes siguen igual)
    @Column(name = "notificarAlertas")
    private Boolean notificarAlertas;

    @Column(name = "notificarLogros")
    private Boolean notificarLogros;

    @Column(name = "notificarRetos")
    private Boolean notificarRetos;

    // Roles del usuario (USUARIO, ADMIN). Se cargan junto al usuario para el login
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private List<Rol> roles = new ArrayList<>();

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

    // US25: ¿acepta las notificaciones automáticas de este tipo ("alerta", "logro" o "reto")?
    public boolean aceptaNotificacion(String tipo) {
        Boolean acepta = switch (tipo == null ? "" : tipo) {
            case "alerta" -> notificarAlertas;
            case "logro" -> notificarLogros;
            case "reto" -> notificarRetos;
            default -> Boolean.TRUE;
        };
        return acepta == null || acepta;
    }

    public Boolean getNotificarAlertas() {
        return notificarAlertas;
    }

    public void setNotificarAlertas(Boolean notificarAlertas) {
        this.notificarAlertas = notificarAlertas;
    }

    public Boolean getNotificarLogros() {
        return notificarLogros;
    }

    public void setNotificarLogros(Boolean notificarLogros) {
        this.notificarLogros = notificarLogros;
    }

    public Boolean getNotificarRetos() {
        return notificarRetos;
    }

    public void setNotificarRetos(Boolean notificarRetos) {
        this.notificarRetos = notificarRetos;
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

    public String getContrasenaUsuario() {
        return contrasenaUsuario;
    }

    public void setContrasenaUsuario(String contrasenaUsuario) {
        this.contrasenaUsuario = contrasenaUsuario;
    }

    public List<Rol> getRoles() {
        return roles;
    }

    public void setRoles(List<Rol> roles) {
        this.roles = roles;
    }
}
