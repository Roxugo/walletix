package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChallengeDTO {
    private int idChallenge;
    private String titleChallenge;
    private String descriptionChallenge;
    private BigDecimal targetAmountChallenge;
    private int rewardPointsChallenge;
    private LocalDate startDateChallenge;
    private LocalDate endDateChallenge;
    private BigDecimal minInitialBalanceChallenge;
    private int minAgeRequirementChallenge;
    private BigDecimal maxDebtAllowedChallenge;
    private boolean customChallenge;

    public int getIdChallenge() {
        return idChallenge;
    }

    public void setIdChallenge(int idChallenge) {
        this.idChallenge = idChallenge;
    }

    public String getTitleChallenge() {
        return titleChallenge;
    }

    public void setTitleChallenge(String titleChallenge) {
        this.titleChallenge = titleChallenge;
    }

    public String getDescriptionChallenge() {
        return descriptionChallenge;
    }

    public void setDescriptionChallenge(String descriptionChallenge) {
        this.descriptionChallenge = descriptionChallenge;
    }

    public BigDecimal getTargetAmountChallenge() {
        return targetAmountChallenge;
    }

    public void setTargetAmountChallenge(BigDecimal targetAmountChallenge) {
        this.targetAmountChallenge = targetAmountChallenge;
    }

    public int getRewardPointsChallenge() {
        return rewardPointsChallenge;
    }

    public void setRewardPointsChallenge(int rewardPointsChallenge) {
        this.rewardPointsChallenge = rewardPointsChallenge;
    }

    public LocalDate getStartDateChallenge() {
        return startDateChallenge;
    }

    public void setStartDateChallenge(LocalDate startDateChallenge) {
        this.startDateChallenge = startDateChallenge;
    }

    public LocalDate getEndDateChallenge() {
        return endDateChallenge;
    }

    public void setEndDateChallenge(LocalDate endDateChallenge) {
        this.endDateChallenge = endDateChallenge;
    }

    public BigDecimal getMinInitialBalanceChallenge() {
        return minInitialBalanceChallenge;
    }

    public void setMinInitialBalanceChallenge(BigDecimal minInitialBalanceChallenge) {
        this.minInitialBalanceChallenge = minInitialBalanceChallenge;
    }

    public int getMinAgeRequirementChallenge() {
        return minAgeRequirementChallenge;
    }

    public void setMinAgeRequirementChallenge(int minAgeRequirementChallenge) {
        this.minAgeRequirementChallenge = minAgeRequirementChallenge;
    }

    public BigDecimal getMaxDebtAllowedChallenge() {
        return maxDebtAllowedChallenge;
    }

    public void setMaxDebtAllowedChallenge(BigDecimal maxDebtAllowedChallenge) {
        this.maxDebtAllowedChallenge = maxDebtAllowedChallenge;
    }

    public boolean isCustomChallenge() {
        return customChallenge;
    }

    public void setCustomChallenge(boolean customChallenge) {
        this.customChallenge = customChallenge;
    }
}