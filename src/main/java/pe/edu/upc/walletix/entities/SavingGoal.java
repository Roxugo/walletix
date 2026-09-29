package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table (name = "tm_savingGoals")
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSavingGoal;

    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "status", nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    public int getIdSavingGoal() {
        return idSavingGoal;
    }

    public void setIdSavingGoal(int idSavingGoal) {
        this.idSavingGoal = idSavingGoal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
