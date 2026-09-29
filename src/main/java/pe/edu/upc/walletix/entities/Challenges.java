package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "challenges")
public class Challenges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idChallenge;

    @Column(name = "titleChallenge", length = 150, nullable = false)
    private String titleChallenge;

    @Column(name = "descriptionChallenge", columnDefinition = "TEXT")
    private String descriptionChallenge;

    @Column(name = "targetAmountChallenge", precision = 12, scale = 2)
    private BigDecimal targetAmountChallenge;

    @Column(name = "rewardPointsChallenge")
    private int rewardPointsChallenge;

    @Column(name = "startDateChallenge", nullable = false)
    private LocalDate startDateChallenge;

    @Column(name = "endDateChallenge", nullable = false)
    private LocalDate endDateChallenge;

    @Column(name = "minInitialBalanceChallenge", precision = 12, scale = 2)
    private BigDecimal minInitialBalanceChallenge;

    @Column(name = "minAgeRequirementChallenge")
    private int minAgeRequirementChallenge;

    @Column(name = "maxDebtAllowedChallenge", precision = 12, scale = 2)
    private BigDecimal maxDebtAllowedChallenge;

    @Column(name = "customChallenge")
    private boolean customChallenge;

    public Challenges() {
    }

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