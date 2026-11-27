package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

// Datos de un gasto
@Schema(description = "Gasto del usuario: se descuenta del saldo")
public class GastoDTO {
    @Schema(description = "Id del gasto (0 al crear)", example = "0")
    private int idGasto;
    @Schema(description = "Dueño del gasto", example = "7")
    private int idUsuario;
    @Schema(description = "Categoría de tipo \"gasto\"", example = "1")
    private int idCategoria;
    @Schema(description = "Comercio de esa categoría", example = "1")
    private int idComerciante;
    @Schema(description = "Descripción (máximo 100 caracteres)", example = "Almuerzo menú")
    private String descripcionGasto;
    @Schema(description = "Monto en soles, mayor que 0 y con máximo 2 decimales", example = "18.50")
    private BigDecimal montoGasto;
    @Schema(description = "Fecha del gasto (no futura; desde el 1 de enero del año anterior)", example = "2026-11-26")
    private LocalDate fechaGasto;
    @Schema(description = "Método de pago", example = "efectivo")
    private String metodoPagoGasto;
    @Schema(description = "true si se repite cada mes", example = "false")
    private boolean fijoGasto;
    @Schema(description = "1 = activo, 0 = eliminado", example = "1")
    private int estadoGasto = 1;

    // US15: solo en la respuesta al registrar; aparece si el gasto superó el presupuesto del mes
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Solo en la respuesta: aviso si se superó el presupuesto del mes", example = "Superaste tu presupuesto de Comida: llevas S/ 530.00 de S/ 500.00")
    private String alertaPresupuesto;

    // Solo en la respuesta: el gasto le quitó un día a un reto en progreso
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Solo en la respuesta: aviso si el gasto rompe un reto en curso", example = "Este gasto rompe tu reto «Semana sin delivery» («Pedido Rappi», S/ 35.00): perdiste ese día")
    private String avisoReto;

    public String getAvisoReto() {
        return avisoReto;
    }

    public void setAvisoReto(String avisoReto) {
        this.avisoReto = avisoReto;
    }

    public String getAlertaPresupuesto() {
        return alertaPresupuesto;
    }

    public void setAlertaPresupuesto(String alertaPresupuesto) {
        this.alertaPresupuesto = alertaPresupuesto;
    }

    public int getIdGasto() {
        return idGasto;
    }

    public void setIdGasto(int idGasto) {
        this.idGasto = idGasto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdComerciante() {
        return idComerciante;
    }

    public void setIdComerciante(int idComerciante) {
        this.idComerciante = idComerciante;
    }

    public String getDescripcionGasto() {
        return descripcionGasto;
    }

    public void setDescripcionGasto(String descripcionGasto) {
        this.descripcionGasto = descripcionGasto;
    }

    public BigDecimal getMontoGasto() {
        return montoGasto;
    }

    public void setMontoGasto(BigDecimal montoGasto) {
        this.montoGasto = montoGasto;
    }

    public LocalDate getFechaGasto() {
        return fechaGasto;
    }

    public void setFechaGasto(LocalDate fechaGasto) {
        this.fechaGasto = fechaGasto;
    }

    public String getMetodoPagoGasto() {
        return metodoPagoGasto;
    }

    public void setMetodoPagoGasto(String metodoPagoGasto) {
        this.metodoPagoGasto = metodoPagoGasto;
    }

    public boolean isFijoGasto() {
        return fijoGasto;
    }

    public void setFijoGasto(boolean fijoGasto) {
        this.fijoGasto = fijoGasto;
    }

    public int getEstadoGasto() {
        return estadoGasto;
    }

    public void setEstadoGasto(int estadoGasto) {
        this.estadoGasto = estadoGasto;
    }
}
