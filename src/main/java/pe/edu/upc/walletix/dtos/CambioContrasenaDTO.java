package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos para que un usuario cambie su propia contraseña
@Schema(description = "Cambio de contraseña del usuario que inició sesión")
public class CambioContrasenaDTO {
    @Schema(description = "Contraseña actual", example = "MiClave123")
    private String contrasenaActual;
    @Schema(description = "Contraseña nueva (mínimo 6 caracteres)", example = "NuevaClave456")
    private String contrasenaNueva;

    public String getContrasenaActual() {
        return contrasenaActual;
    }

    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }

    public String getContrasenaNueva() {
        return contrasenaNueva;
    }

    public void setContrasenaNueva(String contrasenaNueva) {
        this.contrasenaNueva = contrasenaNueva;
    }
}
