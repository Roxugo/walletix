package pe.edu.upc.walletix.dtos;

public class PresupuestoDto {
    private int idPresupuesto;
    private int mes;
    private int anio;
    private float montoAsignado;
    private Usuario idUsuario;
    private Categoria idCategoria;
    private int estado = 1;

    public int getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(int idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public float getMontoAsignado() {
        return montoAsignado;
    }

    public void setMontoAsignado(float montoAsignado) {
        this.montoAsignado = montoAsignado;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Categoria getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Categoria idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
