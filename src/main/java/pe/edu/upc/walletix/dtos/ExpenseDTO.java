package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    private int idExpense;
    private int userIdExpense;
    private int categoryIdExpense;
    private Integer merchantIdExpense;
    private BigDecimal amountExpense;
    private LocalDate dateExpense;
    private String descriptionExpense;
    private String paymentMethodExpense;
    private boolean isMicroexpenseExpense;

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
