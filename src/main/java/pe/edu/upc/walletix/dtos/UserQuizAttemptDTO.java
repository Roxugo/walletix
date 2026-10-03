package pe.edu.upc.walletix.dtos;

// Datos de un intento de quiz (passed lo calcula la API con la nota mínima)
public class UserQuizAttemptDTO {
    private int idUserQuizAttempt;
    private int idUser;
    private int idMicrolesson;
    private int scoreUserQuizAttempt;
    private boolean passedUserQuizAttempt;

    public int getIdUserQuizAttempt() {
        return idUserQuizAttempt;
    }

    public void setIdUserQuizAttempt(int idUserQuizAttempt) {
        this.idUserQuizAttempt = idUserQuizAttempt;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdMicrolesson() {
        return idMicrolesson;
    }

    public void setIdMicrolesson(int idMicrolesson) {
        this.idMicrolesson = idMicrolesson;
    }

    public int getScoreUserQuizAttempt() {
        return scoreUserQuizAttempt;
    }

    public void setScoreUserQuizAttempt(int scoreUserQuizAttempt) {
        this.scoreUserQuizAttempt = scoreUserQuizAttempt;
    }

    public boolean isPassedUserQuizAttempt() {
        return passedUserQuizAttempt;
    }

    public void setPassedUserQuizAttempt(boolean passedUserQuizAttempt) {
        this.passedUserQuizAttempt = passedUserQuizAttempt;
    }
}
