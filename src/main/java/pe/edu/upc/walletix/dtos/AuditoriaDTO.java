package pe.edu.upc.walletix.dtos;

import java.time.LocalDateTime;

public class AuditoriaDTO {
    private int idAuditoria;
    private int idUsuarioRegistro;
    private LocalDateTime fechaRegistro;
    private Integer idUsuarioEditar;
    private LocalDateTime fechaEditar;
    private Integer idUsuarioEliminar;
    private LocalDateTime fechaEliminar;
    private int estado = 1;

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public int getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(int idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getIdUsuarioEditar() {
        return idUsuarioEditar;
    }

    public void setIdUsuarioEditar(Integer idUsuarioEditar) {
        this.idUsuarioEditar = idUsuarioEditar;
    }

    public LocalDateTime getFechaEditar() {
        return fechaEditar;
    }

    public void setFechaEditar(LocalDateTime fechaEditar) {
        this.fechaEditar = fechaEditar;
    }

    public Integer getIdUsuarioEliminar() {
        return idUsuarioEliminar;
    }

    public void setIdUsuarioEliminar(Integer idUsuarioEliminar) {
        this.idUsuarioEliminar = idUsuarioEliminar;
    }

    public LocalDateTime getFechaEliminar() {
        return fechaEliminar;
    }

    public void setFechaEliminar(LocalDateTime fechaEliminar) {
        this.fechaEliminar = fechaEliminar;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
