package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Preguntas del quiz de una microlección
@Entity
@Table(name = "quiz_questions")
public class QuizQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idQuizQuestion;

    @ManyToOne
    @JoinColumn(name = "idMicrolesson", nullable = false)
    private Microlessons microlesson;

    @Column(name = "questionTextQuizQuestion", columnDefinition = "TEXT", nullable = false)
    private String questionTextQuizQuestion;

    @Column(name = "option_a_quiz_question", length = 255, nullable = false)
    private String optionAQuizQuestion;

    @Column(name = "option_b_quiz_question", length = 255, nullable = false)
    private String optionBQuizQuestion;

    @Column(name = "option_c_quiz_question", length = 255)
    private String optionCQuizQuestion;

    @Column(name = "option_d_quiz_question", length = 255)
    private String optionDQuizQuestion;

    // A, B, C o D
    @Column(name = "correctOptionQuizQuestion", length = 1, nullable = false)
    private String correctOptionQuizQuestion;

    @Column(name = "explanationQuizQuestion", columnDefinition = "TEXT")
    private String explanationQuizQuestion;

    public QuizQuestions() {
    }

    public QuizQuestions(int idQuizQuestion, Microlessons microlesson, String questionTextQuizQuestion, String optionAQuizQuestion, String optionBQuizQuestion, String optionCQuizQuestion, String optionDQuizQuestion, String correctOptionQuizQuestion, String explanationQuizQuestion) {
        this.idQuizQuestion = idQuizQuestion;
        this.microlesson = microlesson;
        this.questionTextQuizQuestion = questionTextQuizQuestion;
        this.optionAQuizQuestion = optionAQuizQuestion;
        this.optionBQuizQuestion = optionBQuizQuestion;
        this.optionCQuizQuestion = optionCQuizQuestion;
        this.optionDQuizQuestion = optionDQuizQuestion;
        this.correctOptionQuizQuestion = correctOptionQuizQuestion;
        this.explanationQuizQuestion = explanationQuizQuestion;
    }

    public int getIdQuizQuestion() {
        return idQuizQuestion;
    }

    public void setIdQuizQuestion(int idQuizQuestion) {
        this.idQuizQuestion = idQuizQuestion;
    }

    public Microlessons getMicrolesson() {
        return microlesson;
    }

    public void setMicrolesson(Microlessons microlesson) {
        this.microlesson = microlesson;
    }

    public String getQuestionTextQuizQuestion() {
        return questionTextQuizQuestion;
    }

    public void setQuestionTextQuizQuestion(String questionTextQuizQuestion) {
        this.questionTextQuizQuestion = questionTextQuizQuestion;
    }

    public String getOptionAQuizQuestion() {
        return optionAQuizQuestion;
    }

    public void setOptionAQuizQuestion(String optionAQuizQuestion) {
        this.optionAQuizQuestion = optionAQuizQuestion;
    }

    public String getOptionBQuizQuestion() {
        return optionBQuizQuestion;
    }

    public void setOptionBQuizQuestion(String optionBQuizQuestion) {
        this.optionBQuizQuestion = optionBQuizQuestion;
    }

    public String getOptionCQuizQuestion() {
        return optionCQuizQuestion;
    }

    public void setOptionCQuizQuestion(String optionCQuizQuestion) {
        this.optionCQuizQuestion = optionCQuizQuestion;
    }

    public String getOptionDQuizQuestion() {
        return optionDQuizQuestion;
    }

    public void setOptionDQuizQuestion(String optionDQuizQuestion) {
        this.optionDQuizQuestion = optionDQuizQuestion;
    }

    public String getCorrectOptionQuizQuestion() {
        return correctOptionQuizQuestion;
    }

    public void setCorrectOptionQuizQuestion(String correctOptionQuizQuestion) {
        this.correctOptionQuizQuestion = correctOptionQuizQuestion;
    }

    public String getExplanationQuizQuestion() {
        return explanationQuizQuestion;
    }

    public void setExplanationQuizQuestion(String explanationQuizQuestion) {
        this.explanationQuizQuestion = explanationQuizQuestion;
    }
}
