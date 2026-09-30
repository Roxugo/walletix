package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idExpense;

    @Column(name = "user_id", nullable = false)
    private int userIdExpense;

    @Column(name = "category_id", nullable = false)
    private int categoryIdExpense;

    @Column(name = "merchant_id")
    private Integer merchantIdExpense;

    @Column(name = "amount", nullable = false)
    private BigDecimal amountExpense;

    @Column(name = "date", nullable = false)
    private LocalDate dateExpense;

    @Column(name = "description")
    private String descriptionExpense;

    @Column(name = "payment_method", length = 50)
    private String paymentMethodExpense;

    @Column(name = "is_microexpense")
    private boolean isMicroexpenseExpense;

    public Expense() {
    }

    public Expense(int idExpense, int userIdExpense, int categoryIdExpense, Integer merchantIdExpense, BigDecimal amountExpense, LocalDate dateExpense, String descriptionExpense, String paymentMethodExpense, boolean isMicroexpenseExpense) {
        this.idExpense = idExpense;
        this.userIdExpense = userIdExpense;
        this.categoryIdExpense = categoryIdExpense;
        this.merchantIdExpense = merchantIdExpense;
        this.amountExpense = amountExpense;
        this.dateExpense = dateExpense;
        this.descriptionExpense = descriptionExpense;
        this.paymentMethodExpense = paymentMethodExpense;
        this.isMicroexpenseExpense = isMicroexpenseExpense;
    }

    public int getIdExpense() {
        return idExpense;
    }

    public void setIdExpense(int idExpense) {
        this.idExpense = idExpense;
    }

    public int getUserIdExpense() {
        return userIdExpense;
    }

    public void setUserIdExpense(int userIdExpense) {
        this.userIdExpense = userIdExpense;
    }

    public int getCategoryIdExpense() {
        return categoryIdExpense;
    }

    public void setCategoryIdExpense(int categoryIdExpense) {
        this.categoryIdExpense = categoryIdExpense;
    }

    public Integer getMerchantIdExpense() {
        return merchantIdExpense;
    }

    public void setMerchantIdExpense(Integer merchantIdExpense) {
        this.merchantIdExpense = merchantIdExpense;
    }

    public BigDecimal getAmountExpense() {
        return amountExpense;
    }

    public void setAmountExpense(BigDecimal amountExpense) {
        this.amountExpense = amountExpense;
    }

    public LocalDate getDateExpense() {
        return dateExpense;
    }

    public void setDateExpense(LocalDate dateExpense) {
        this.dateExpense = dateExpense;
    }

    public String getDescriptionExpense() {
        return descriptionExpense;
    }

    public void setDescriptionExpense(String descriptionExpense) {
        this.descriptionExpense = descriptionExpense;
    }

    public String getPaymentMethodExpense() {
        return paymentMethodExpense;
    }

    public void setPaymentMethodExpense(String paymentMethodExpense) {
        this.paymentMethodExpense = paymentMethodExpense;
    }

    public boolean isMicroexpenseExpense() {
        return isMicroexpenseExpense;
    }

    public void setMicroexpenseExpense(boolean microexpenseExpense) {
        isMicroexpenseExpense = microexpenseExpense;
    }
}
