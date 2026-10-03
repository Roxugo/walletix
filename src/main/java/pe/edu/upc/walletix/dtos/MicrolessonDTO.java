package pe.edu.upc.walletix.dtos;

// Datos de una microlección o consejo
public class MicrolessonDTO {
    private int idMicrolesson;
    private String titleMicrolesson;
    private String descriptionMicrolesson;
    private String contentTextMicrolesson;
    private String mediaUrlMicrolesson;
    private String categoryMicrolesson;
    private String typeMicrolesson;
    private boolean lockedMicrolesson;
    private String quizTitleMicrolesson;
    private int passingScoreMicrolesson;
    private int rewardPointsMicrolesson;

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
