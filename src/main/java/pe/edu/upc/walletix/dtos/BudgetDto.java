package pe.edu.upc.walletix.dtos;

public class BudgetDto {
    private int idBudget;
    private int month;
    private int year;
    private float allocatedAmount;
    private int idUser;
    private int idCategory;

    public int getIdBudget() {
        return idBudget;
    }

    public void setIdBudget(int idBudget) {
        this.idBudget = idBudget;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(float allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
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
