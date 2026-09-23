package org.upc.walletix_gustabo.dtos;

public class BudgetDto {
    private int idBudget;
    private int mes;
    private int anio;
    private float importeAsignado;
    private int idUser;
    private int idCategory;

    public int getIdBudget() {
        return idBudget;
    }

    public void setIdBudget(int idBudget) {
        this.idBudget = idBudget;
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

    public float getImporteAsignado() {
        return importeAsignado;
    }

    public void setImporteAsignado(float importeAsignado) {
        this.importeAsignado = importeAsignado;
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
