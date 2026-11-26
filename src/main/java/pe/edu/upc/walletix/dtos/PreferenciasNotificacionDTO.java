package pe.edu.upc.walletix.dtos;

// US25: qué notificaciones automáticas quiere recibir el usuario
public record PreferenciasNotificacionDTO(Boolean alerta, Boolean logro, Boolean reto) {
}
