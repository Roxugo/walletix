package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;

public class UserChallengeDTO {
    private int idUserChallenge;
    private int idUser;
    private int idChallenge;
    private BigDecimal initialBalanceUserChallenge;
    private BigDecimal currentProgressAmountUserChallenge;
    private BigDecimal progressPercentageUserChallenge;
    private String statusUserChallenge;

    public int getIdUserChallenge() {
        return idUserChallenge;
    }

    public void setIdUserChallenge(int idUserChallenge) {
        this.idUserChallenge = idUserChallenge;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdChallenge() {
        return idChallenge;
    }

    public void setIdChallenge(int idChallenge) {
        this.idChallenge = idChallenge;
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