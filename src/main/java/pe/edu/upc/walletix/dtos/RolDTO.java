package pe.edu.upc.walletix.dtos;

public class RolDTO {
    private int idRol;
    private String rol;
    private int idUsuario;
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
