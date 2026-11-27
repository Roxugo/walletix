package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// US25: qué notificaciones automáticas quiere recibir el usuario
@Schema(description = "Qué notificaciones automáticas quiere recibir el usuario (US25); los tres son obligatorios")
public record PreferenciasNotificacionDTO(
        @Schema(description = "Alertas de presupuesto", example = "true") Boolean alerta,
        @Schema(description = "Logros desbloqueados", example = "true") Boolean logro,
        @Schema(description = "Retos superados o días perdidos", example = "false") Boolean reto) {
}
