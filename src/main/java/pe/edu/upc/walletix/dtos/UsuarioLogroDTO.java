package pe.edu.upc.walletix.dtos;

public class UsuarioLogroDTO {
    private int idUsuarioLogro;
    private int estadoUsuarioLogro = 1;
    private int idUsuario;
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
