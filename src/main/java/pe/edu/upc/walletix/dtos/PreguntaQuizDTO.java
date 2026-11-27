package pe.edu.upc.walletix.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

// Datos de una pregunta del quiz
@Schema(description = "Pregunta del quiz de una microlección (la crea un ADMIN)")
public class PreguntaQuizDTO {
    @Schema(description = "Id (0 al crear)", example = "0")
    private int idPreguntaQuiz;
    @Schema(description = "Microlección", example = "1")
    private int idMicroleccion;
    @Schema(description = "Pregunta", example = "Con un ingreso de S/ 1,500, ¿cuánto deberías ahorrar según la regla 50/30/20?")
    private String enunciadoPreguntaQuiz;
    @Schema(description = "Opción a", example = "S/ 150")
    private String opcionAPreguntaQuiz;
    @Schema(description = "Opción b", example = "S/ 300")
    private String opcionBPreguntaQuiz;
    @Schema(description = "Opción c", example = "S/ 450")
    private String opcionCPreguntaQuiz;
    @Schema(description = "Opción d", example = "S/ 750")
    private String opcionDPreguntaQuiz;
    @Schema(description = "Letra correcta (nunca se envía al navegador)", example = "b")
    private String opcionCorrectaPreguntaQuiz;
    @Schema(description = "Explicación que se muestra después de responder", example = "El 20% de S/ 1,500 es S/ 300.")
    private String explicacionPreguntaQuiz;
    @Schema(description = "1 = activa, 0 = eliminada", example = "1")
    private int estadoPreguntaQuiz = 1;

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

    public int getEstadoPreguntaQuiz() {
        return estadoPreguntaQuiz;
    }

    public void setEstadoPreguntaQuiz(int estadoPreguntaQuiz) {
        this.estadoPreguntaQuiz = estadoPreguntaQuiz;
    }
}
