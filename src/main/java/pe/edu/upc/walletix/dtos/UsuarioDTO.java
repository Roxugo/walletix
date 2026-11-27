package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Datos de una cuenta de usuario")
public class UsuarioDTO {
    @Schema(description = "Id del usuario (0 al registrarse)", example = "0")
    private int idUsuario;
    @Schema(description = "Nombre completo", example = "Ana Quispe")
    private String nombreUsuario;
    @Schema(description = "Correo único de la cuenta", example = "ana.quispe@gmail.com")
    private String correoUsuario;
    // Solo se recibe (al registrarse); nunca se devuelve en las respuestas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Contraseña (solo al registrarse; se guarda cifrada con BCrypt)", example = "MiClave123")
    private String contrasenaUsuario;
    @Schema(description = "Celular de 9 dígitos", example = "987654321")
    private int telefonoUsuario;
    @Schema(description = "Fecha de nacimiento (no futura)", example = "2003-05-14")
    private LocalDate fechaNacimientoUsuario;
    @Schema(description = "Segmento del usuario", example = "estudiante")
    private String segmentoUsuario;
    @Schema(description = "Saldo actual en soles (S/)", example = "1500.00")
    private BigDecimal saldoActualUsuario;
    @Schema(description = "Puntos ganados (solo un ADMIN puede cambiarlos)", example = "0")
    private int puntosGamificacionUsuario;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
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

    public String getContrasenaUsuario() {
        return contrasenaUsuario;
    }

    public void setContrasenaUsuario(String contrasenaUsuario) {
        this.contrasenaUsuario = contrasenaUsuario;
    }
}
