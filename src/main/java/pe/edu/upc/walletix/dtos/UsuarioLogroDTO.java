package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Logro obtenido por un usuario")
public class UsuarioLogroDTO {
    @Schema(description = "Id del registro", example = "0")
    private int idUsuarioLogro;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoUsuarioLogro = 1;
    @Schema(description = "Usuario", example = "7")
    private int idUsuario;
    @Schema(description = "Logro", example = "2")
    private int idLogro;

    public int getIdUsuarioLogro() {
        return idUsuarioLogro;
    }

    public void setIdUsuarioLogro(int idUsuarioLogro) {
        this.idUsuarioLogro = idUsuarioLogro;
    }

    public int getEstadoUsuarioLogro() {
        return estadoUsuarioLogro;
    }

    public void setEstadoUsuarioLogro(int estadoUsuarioLogro) {
        this.estadoUsuarioLogro = estadoUsuarioLogro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdLogro() {
        return idLogro;
    }

    public void setIdLogro(int idLogro) {
        this.idLogro = idLogro;
    }
}
