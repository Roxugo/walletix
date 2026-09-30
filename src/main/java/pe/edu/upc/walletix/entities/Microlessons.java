package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Consejos (tip) y microlecciones con quiz (leccion) de educación financiera
@Entity
@Table(name = "microlessons")
public class Microlessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMicrolesson;

    @Column(name = "titleMicrolesson", length = 150, nullable = false)
    private String titleMicrolesson;

    @Column(name = "descriptionMicrolesson", columnDefinition = "TEXT")
    private String descriptionMicrolesson;

    @Column(name = "contentTextMicrolesson", columnDefinition = "TEXT", nullable = false)
    private String contentTextMicrolesson;

    @Column(name = "mediaUrlMicrolesson", length = 255)
    private String mediaUrlMicrolesson;

    // Ahorro, Inversión, Deudas...
    @Column(name = "categoryMicrolesson", length = 50, nullable = false)
    private String categoryMicrolesson;

    // tip o leccion
    @Column(name = "typeMicrolesson", length = 20, nullable = false)
    private String typeMicrolesson;

    @Column(name = "isLockedMicrolesson")
    private boolean lockedMicrolesson;

    @Column(name = "quizTitleMicrolesson", length = 150)
    private String quizTitleMicrolesson;

    // Nota mínima (0 a 100) para aprobar el quiz
    @Column(name = "passingScoreMicrolesson")
    private int passingScoreMicrolesson;

    @Column(name = "rewardPointsMicrolesson")
    private int rewardPointsMicrolesson;

    public Microlessons() {
    }

    public Microlessons(int idMicrolesson, String titleMicrolesson, String descriptionMicrolesson, String contentTextMicrolesson, String mediaUrlMicrolesson, String categoryMicrolesson, String typeMicrolesson, boolean lockedMicrolesson, String quizTitleMicrolesson, int passingScoreMicrolesson, int rewardPointsMicrolesson) {
        this.idMicrolesson = idMicrolesson;
        this.titleMicrolesson = titleMicrolesson;
        this.descriptionMicrolesson = descriptionMicrolesson;
        this.contentTextMicrolesson = contentTextMicrolesson;
        this.mediaUrlMicrolesson = mediaUrlMicrolesson;
        this.categoryMicrolesson = categoryMicrolesson;
        this.typeMicrolesson = typeMicrolesson;
        this.lockedMicrolesson = lockedMicrolesson;
        this.quizTitleMicrolesson = quizTitleMicrolesson;
        this.passingScoreMicrolesson = passingScoreMicrolesson;
        this.rewardPointsMicrolesson = rewardPointsMicrolesson;
    }

    public int getIdMicrolesson() {
        return idMicrolesson;
    }

    public void setIdMicrolesson(int idMicrolesson) {
        this.idMicrolesson = idMicrolesson;
    }

    public String getTitleMicrolesson() {
        return titleMicrolesson;
    }

    public void setTitleMicrolesson(String titleMicrolesson) {
        this.titleMicrolesson = titleMicrolesson;
    }

    public String getDescriptionMicrolesson() {
        return descriptionMicrolesson;
    }

    public void setDescriptionMicrolesson(String descriptionMicrolesson) {
        this.descriptionMicrolesson = descriptionMicrolesson;
    }

    public String getContentTextMicrolesson() {
        return contentTextMicrolesson;
    }

    public void setContentTextMicrolesson(String contentTextMicrolesson) {
        this.contentTextMicrolesson = contentTextMicrolesson;
    }

    public String getMediaUrlMicrolesson() {
        return mediaUrlMicrolesson;
    }

    public void setMediaUrlMicrolesson(String mediaUrlMicrolesson) {
        this.mediaUrlMicrolesson = mediaUrlMicrolesson;
    }

    public String getCategoryMicrolesson() {
        return categoryMicrolesson;
    }

    public void setCategoryMicrolesson(String categoryMicrolesson) {
        this.categoryMicrolesson = categoryMicrolesson;
    }

    public String getTypeMicrolesson() {
        return typeMicrolesson;
    }

    public void setTypeMicrolesson(String typeMicrolesson) {
        this.typeMicrolesson = typeMicrolesson;
    }

    public boolean isLockedMicrolesson() {
        return lockedMicrolesson;
    }

    public void setLockedMicrolesson(boolean lockedMicrolesson) {
        this.lockedMicrolesson = lockedMicrolesson;
    }

    public String getQuizTitleMicrolesson() {
        return quizTitleMicrolesson;
    }

    public void setQuizTitleMicrolesson(String quizTitleMicrolesson) {
        this.quizTitleMicrolesson = quizTitleMicrolesson;
    }

    public int getPassingScoreMicrolesson() {
        return passingScoreMicrolesson;
    }

    public void setPassingScoreMicrolesson(int passingScoreMicrolesson) {
        this.passingScoreMicrolesson = passingScoreMicrolesson;
    }

    public int getRewardPointsMicrolesson() {
        return rewardPointsMicrolesson;
    }

    public void setRewardPointsMicrolesson(int rewardPointsMicrolesson) {
        this.rewardPointsMicrolesson = rewardPointsMicrolesson;
    }
}
