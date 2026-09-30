package pe.edu.upc.walletix.dtos;

// Datos de una pregunta del quiz
public class QuizQuestionDTO {
    private int idQuizQuestion;
    private int idMicrolesson;
    private String questionTextQuizQuestion;
    private String optionAQuizQuestion;
    private String optionBQuizQuestion;
    private String optionCQuizQuestion;
    private String optionDQuizQuestion;
    private String correctOptionQuizQuestion;
    private String explanationQuizQuestion;

    public int getIdQuizQuestion() {
        return idQuizQuestion;
    }

    public void setIdQuizQuestion(int idQuizQuestion) {
        this.idQuizQuestion = idQuizQuestion;
    }

    public int getIdMicrolesson() {
        return idMicrolesson;
    }

    public void setIdMicrolesson(int idMicrolesson) {
        this.idMicrolesson = idMicrolesson;
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
