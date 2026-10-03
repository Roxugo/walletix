package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_challenges")
public class UserChallenges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUserChallenge;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "idChallenge", nullable = false)
    private Challenges challenge;

    @Column(name = "initialBalanceUserChallenge", precision = 12, scale = 2)
    private BigDecimal initialBalanceUserChallenge;

    @Column(name = "currentProgressAmountUserChallenge", precision = 12, scale = 2)
    private BigDecimal currentProgressAmountUserChallenge;

    @Column(name = "progressPercentageUserChallenge", precision = 5, scale = 2)
    private BigDecimal progressPercentageUserChallenge;

    @Column(name = "statusUserChallenge", length = 20)
    private String statusUserChallenge;

    public UserChallenges() {
    }

    public int getIdUserChallenge() {
        return idUserChallenge;
    }

    public void setIdUserChallenge(int idUserChallenge) {
        this.idUserChallenge = idUserChallenge;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Challenges getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenges challenge) {
        this.challenge = challenge;
    }

    public BigDecimal getInitialBalanceUserChallenge() {
        return initialBalanceUserChallenge;
    }

    public void setInitialBalanceUserChallenge(BigDecimal initialBalanceUserChallenge) {
        this.initialBalanceUserChallenge = initialBalanceUserChallenge;
    }

    public BigDecimal getCurrentProgressAmountUserChallenge() {
        return currentProgressAmountUserChallenge;
    }

    public void setCurrentProgressAmountUserChallenge(BigDecimal currentProgressAmountUserChallenge) {
        this.currentProgressAmountUserChallenge = currentProgressAmountUserChallenge;
    }

    public BigDecimal getProgressPercentageUserChallenge() {
        return progressPercentageUserChallenge;
    }

    public void setProgressPercentageUserChallenge(BigDecimal progressPercentageUserChallenge) {
        this.progressPercentageUserChallenge = progressPercentageUserChallenge;
    }

    public String getStatusUserChallenge() {
        return statusUserChallenge;
    }

    public void setStatusUserChallenge(String statusUserChallenge) {
        this.statusUserChallenge = statusUserChallenge;
    }
}