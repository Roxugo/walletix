package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rol de un usuario")
public class RolDTO {
    @Schema(description = "Id del rol", example = "0")
    private int idRol;
    @Schema(description = "\"USUARIO\" o \"ADMIN\"", example = "USUARIO")
    private String rol;
    @Schema(description = "Usuario", example = "7")
    private int idUsuario;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoRol = 1;

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getEstadoRol() {
        return estadoRol;
    }

    public void setEstadoRol(int estadoRol) {
        this.estadoRol = estadoRol;
    }
}
