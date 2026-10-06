package pe.edu.upc.walletix.dtos;

// Datos que se envían para iniciar sesión (US02: correo y contraseña)
public class JwtRequestDTO {
    private String correo;
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
