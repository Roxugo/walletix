package pe.edu.upc.walletix.dtos;

// Resultado del query nativo: progreso del usuario por microlección
public class LearningProgressDTO {
    private int idMicrolesson;
    private String titleMicrolesson;
    private int attempts;
    private int bestScore;
    private boolean passed;

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

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
