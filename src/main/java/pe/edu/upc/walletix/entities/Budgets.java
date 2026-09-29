package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name  = "tm_budgets")
public class Budgets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBudget;

    @Column (name = "month", nullable = false)
    private int month;

    @Column (name = "year", nullable = false)
    private int year;

    @Column (name = "allocatedAmount", nullable = false)
    private float allocatedAmount;

    @ManyToOne
    @JoinColumn (name = "idUser")
    private User user;

    @ManyToOne
    @JoinColumn (name = "idCategory")
    private CategoryEntity category;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
