package pe.edu.upc.walletix.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

// Datos de un gasto
public class GastoDTO {
    private int idGasto;
    private int idUsuario;
    private int idCategoria;
    private int idComerciante;
    private String descripcionGasto;
    private BigDecimal montoGasto;
    private LocalDate fechaGasto;
    private String metodoPagoGasto;
    private boolean fijoGasto;
    private int estadoGasto = 1;

    // US15: solo en la respuesta al registrar; aparece si el gasto superó el presupuesto del mes
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String alertaPresupuesto;

    // Solo en la respuesta: el gasto le quitó un día a un reto en progreso
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
