package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos que se envían para iniciar sesión (US02: correo y contraseña)
@Schema(description = "Credenciales para iniciar sesión (POST /login)")
public class JwtRequestDTO {
    @Schema(description = "Correo con el que se registró el usuario", example = "ana.quispe@gmail.com")
    private String correo;
    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "MiClave123")
    private String contrasena;

    public JwtRequestDTO() {
    }

    public JwtRequestDTO(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
