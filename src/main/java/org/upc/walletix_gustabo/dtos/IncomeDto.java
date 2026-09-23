package org.upc.walletix_gustabo.dtos;

import org.upc.walletix_gustabo.entity.IncomeFrequency;

import java.time.LocalDate;

public class IncomeDto {
    private int idIncome;
    private float cantidad;
    private LocalDate fecha;
    private String tipoIngreso;
    private IncomeFrequency frequency;
    private String source;
    private String description;
    private int idUser;
    private int idCategory;

    public int getIdIncome() {
        return idIncome;
    }

    public void setIdIncome(int idIncome) {
        this.idIncome = idIncome;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoIngreso() {
        return tipoIngreso;
    }

    public void setTipoIngreso(String tipoIngreso) {
        this.tipoIngreso = tipoIngreso;
    }

    public IncomeFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(IncomeFrequency frequency) {
        this.frequency = frequency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }
}
