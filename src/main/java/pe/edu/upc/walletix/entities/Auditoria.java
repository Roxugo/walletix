package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAuditoria;

    // Fase 1: Creación / Registro (Obligatorios)
    @ManyToOne
    @JoinColumn(name = "idUsuarioRegistro", nullable = false)
    private Usuario usuarioRegistro;

    @Column(name = "fechaRegistro", nullable = false)
    private LocalDateTime fechaRegistro;

    // Fase 2: Edición / Actualización (Opcionales)
    @ManyToOne
    @JoinColumn(name = "idUsuarioEditar", nullable = true)
    private Usuario usuarioEditar;

    @Column(name = "fechaEditar", nullable = true)
    private LocalDateTime fechaEditar;

    // Fase 3: Eliminación / Baja lógica (Opcionales)
    @ManyToOne
    @JoinColumn(name = "idUsuarioEliminar", nullable = true)
    private Usuario usuarioEliminar;

    @Column(name = "fechaEliminar", nullable = true)
    private LocalDateTime fechaEliminar;

    // Bandera operativa de estado (true: Activo, false: Inactivo)
    @Column(name = "estado", nullable = false)
    private boolean estado = true;

    public Auditoria() {
    }

    public Auditoria(int idAuditoria, Usuario usuarioRegistro, LocalDateTime fechaRegistro, Usuario usuarioEditar, LocalDateTime fechaEditar, Usuario usuarioEliminar, LocalDateTime fechaEliminar, boolean estado) {
        this.idAuditoria = idAuditoria;
        this.usuarioRegistro = usuarioRegistro;
        this.fechaRegistro = fechaRegistro;
        this.usuarioEditar = usuarioEditar;
        this.fechaEditar = fechaEditar;
        this.usuarioEliminar = usuarioEliminar;
        this.fechaEliminar = fechaEliminar;
        this.estado = estado;
    }

    public int getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(int idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getUsuarioEditar() {
        return usuarioEditar;
    }

    public void setUsuarioEditar(Usuario usuarioEditar) {
        this.usuarioEditar = usuarioEditar;
    }

    public LocalDateTime getFechaEditar() {
        return fechaEditar;
    }

    public void setFechaEditar(LocalDateTime fechaEditar) {
        this.fechaEditar = fechaEditar;
    }

    public Usuario getUsuarioEliminar() {
        return usuarioEliminar;
    }

    public void setUsuarioEliminar(Usuario usuarioEliminar) {
        this.usuarioEliminar = usuarioEliminar;
    }

    public LocalDateTime getFechaEliminar() {
        return fechaEliminar;
    }

    public void setFechaEliminar(LocalDateTime fechaEliminar) {
        this.fechaEliminar = fechaEliminar;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
