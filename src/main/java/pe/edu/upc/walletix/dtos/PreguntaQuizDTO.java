package pe.edu.upc.walletix.dtos;

// Datos de una pregunta del quiz
public class PreguntaQuizDTO {
    private int idPreguntaQuiz;
    private int idMicroleccion;
    private String enunciadoPreguntaQuiz;
    private String opcionAPreguntaQuiz;
    private String opcionBPreguntaQuiz;
    private String opcionCPreguntaQuiz;
    private String opcionDPreguntaQuiz;
    private String opcionCorrectaPreguntaQuiz;
    private String explicacionPreguntaQuiz;

    public int getIdPreguntaQuiz() {
        return idPreguntaQuiz;
    }

    public void setIdPreguntaQuiz(int idPreguntaQuiz) {
        this.idPreguntaQuiz = idPreguntaQuiz;
    }

    public int getIdMicroleccion() {
        return idMicroleccion;
    }

    public void setIdMicroleccion(int idMicroleccion) {
        this.idMicroleccion = idMicroleccion;
    }

    public String getEnunciadoPreguntaQuiz() {
        return enunciadoPreguntaQuiz;
    }

    public void setEnunciadoPreguntaQuiz(String enunciadoPreguntaQuiz) {
        this.enunciadoPreguntaQuiz = enunciadoPreguntaQuiz;
    }

    public String getOpcionAPreguntaQuiz() {
        return opcionAPreguntaQuiz;
    }

    public void setOpcionAPreguntaQuiz(String opcionAPreguntaQuiz) {
        this.opcionAPreguntaQuiz = opcionAPreguntaQuiz;
    }

    public String getOpcionBPreguntaQuiz() {
        return opcionBPreguntaQuiz;
    }

    public void setOpcionBPreguntaQuiz(String opcionBPreguntaQuiz) {
        this.opcionBPreguntaQuiz = opcionBPreguntaQuiz;
    }

    public String getOpcionCPreguntaQuiz() {
        return opcionCPreguntaQuiz;
    }

    public void setOpcionCPreguntaQuiz(String opcionCPreguntaQuiz) {
        this.opcionCPreguntaQuiz = opcionCPreguntaQuiz;
    }

    public String getOpcionDPreguntaQuiz() {
        return opcionDPreguntaQuiz;
    }

    public void setOpcionDPreguntaQuiz(String opcionDPreguntaQuiz) {
        this.opcionDPreguntaQuiz = opcionDPreguntaQuiz;
    }

    public String getOpcionCorrectaPreguntaQuiz() {
        return opcionCorrectaPreguntaQuiz;
    }

    public void setOpcionCorrectaPreguntaQuiz(String opcionCorrectaPreguntaQuiz) {
        this.opcionCorrectaPreguntaQuiz = opcionCorrectaPreguntaQuiz;
    }

    public String getExplicacionPreguntaQuiz() {
        return explicacionPreguntaQuiz;
    }

    public void setExplicacionPreguntaQuiz(String explicacionPreguntaQuiz) {
        this.explicacionPreguntaQuiz = explicacionPreguntaQuiz;
    }
}
