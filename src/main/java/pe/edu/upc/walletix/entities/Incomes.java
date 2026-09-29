package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tm_incomes")
public class Incomes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idIncome")
    private int idIncome;

    @Column (name = "amount", nullable = false)
    private float amount;

    @Column (name = "date", nullable = false)
    private LocalDate date;

    @Column (name = "income_type", length = 100, nullable = false)
    private String incomeType;

    @Column (name = "frequency", nullable = false)
    private String frequency;

    @Column (name = "source", length = 100, nullable = false)
    private String source;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn (name = "idUser")
    private User user;

    @ManyToOne
    @JoinColumn (name = "idCategory")
    private CategoryEntity category;

    public int getIdIncome() {
        return idIncome;
    }

    public void setIdIncome(int idIncome) {
        this.idIncome = idIncome;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
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
