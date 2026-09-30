package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Cada vez que un usuario rinde el quiz de una microlección
@Entity
@Table(name = "user_quiz_attempts")
public class UserQuizAttempts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUserQuizAttempt;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "idMicrolesson", nullable = false)
    private Microlessons microlesson;

    // Nota de 0 a 100
    @Column(name = "scoreUserQuizAttempt", nullable = false)
    private int scoreUserQuizAttempt;

    // true si la nota es mayor o igual a la nota mínima de la microlección
    @Column(name = "isPassedUserQuizAttempt")
    private boolean passedUserQuizAttempt;

    public UserQuizAttempts() {
    }

    public UserQuizAttempts(int idUserQuizAttempt, Users user, Microlessons microlesson, int scoreUserQuizAttempt, boolean passedUserQuizAttempt) {
        this.idUserQuizAttempt = idUserQuizAttempt;
        this.user = user;
        this.microlesson = microlesson;
        this.scoreUserQuizAttempt = scoreUserQuizAttempt;
        this.passedUserQuizAttempt = passedUserQuizAttempt;
    }

    public int getIdUserQuizAttempt() {
        return idUserQuizAttempt;
    }

    public void setIdUserQuizAttempt(int idUserQuizAttempt) {
        this.idUserQuizAttempt = idUserQuizAttempt;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Microlessons getMicrolesson() {
        return microlesson;
    }

    public void setMicrolesson(Microlessons microlesson) {
        this.microlesson = microlesson;
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
