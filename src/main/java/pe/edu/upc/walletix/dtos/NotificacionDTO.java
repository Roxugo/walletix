package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Schema(description = "Notificación de la campana")
public class NotificacionDTO {
    @Schema(description = "Id de la notificación (0 al crear)", example = "0")
    private int idNotificacion;
    @Schema(description = "Usuario que la recibe", example = "7")
    private int idUsuario;
    @Schema(description = "Título (máximo 30 caracteres)", example = "Presupuesto excedido")
    private String titulo;
    @Schema(description = "\"alerta\", \"logro\", \"reto\" u otro", example = "alerta")
    private String tipo;
    @Schema(description = "Mensaje (máximo 100 caracteres)", example = "Superaste tu presupuesto de Comida: llevas S/ 530.00 de S/ 500.00")
    private String mensaje;
    @Schema(description = "true = leída, false = no leída", example = "false")
    private boolean estadoNotificacion; // true = leída, false = no leída
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
    private int estado = 1;
    // La pone la API al registrar la notificación
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Fecha y hora (hora de Perú)", example = "2026-11-26T18:30:00")
    private LocalDateTime fechaNotificacion;

    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(LocalDateTime fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public int getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(int idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isEstadoNotificacion() {
        return estadoNotificacion;
    }

    public void setEstadoNotificacion(boolean estadoNotificacion) {
        this.estadoNotificacion = estadoNotificacion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}