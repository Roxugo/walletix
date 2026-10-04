package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Preguntas de opción múltiple del quiz de una microlección
@Entity
@Table(name = "PreguntaQuiz")
public class PreguntaQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPreguntaQuiz;

    @ManyToOne
    @JoinColumn(name = "idMicroleccion", nullable = false)
    private Microleccion microleccion;

    @Column(name = "enunciadoPreguntaQuiz", columnDefinition = "TEXT", nullable = false)
    private String enunciadoPreguntaQuiz;

    @Column(name = "opcion_a_pregunta_quiz", length = 255, nullable = false)
    private String opcionAPreguntaQuiz;

    @Column(name = "opcion_b_pregunta_quiz", length = 255, nullable = false)
    private String opcionBPreguntaQuiz;

    // C y D son opcionales: hay preguntas de solo dos opciones
    @Column(name = "opcion_c_pregunta_quiz", length = 255)
    private String opcionCPreguntaQuiz;

    @Column(name = "opcion_d_pregunta_quiz", length = 255)
    private String opcionDPreguntaQuiz;

    // A, B, C o D
    @Column(name = "opcionCorrectaPreguntaQuiz", length = 1, nullable = false)
    private String opcionCorrectaPreguntaQuiz;

    @Column(name = "explicacionPreguntaQuiz", columnDefinition = "TEXT", nullable = false)
    private String explicacionPreguntaQuiz;

    // Borrado lógico: 1 = activo, 0 = eliminado
    @Column(name = "estadoPreguntaQuiz", nullable = false)
    private Integer estadoPreguntaQuiz = 1;

    public PreguntaQuiz() {
    }

    public PreguntaQuiz(int idPreguntaQuiz, Microleccion microleccion, String enunciadoPreguntaQuiz, String opcionAPreguntaQuiz, String opcionBPreguntaQuiz, String opcionCPreguntaQuiz, String opcionDPreguntaQuiz, String opcionCorrectaPreguntaQuiz, String explicacionPreguntaQuiz, Integer estadoPreguntaQuiz) {
        this.idPreguntaQuiz = idPreguntaQuiz;
        this.microleccion = microleccion;
        this.enunciadoPreguntaQuiz = enunciadoPreguntaQuiz;
        this.opcionAPreguntaQuiz = opcionAPreguntaQuiz;
        this.opcionBPreguntaQuiz = opcionBPreguntaQuiz;
        this.opcionCPreguntaQuiz = opcionCPreguntaQuiz;
        this.opcionDPreguntaQuiz = opcionDPreguntaQuiz;
        this.opcionCorrectaPreguntaQuiz = opcionCorrectaPreguntaQuiz;
        this.explicacionPreguntaQuiz = explicacionPreguntaQuiz;
        this.estadoPreguntaQuiz = estadoPreguntaQuiz;
    }

    public int getIdPreguntaQuiz() {
        return idPreguntaQuiz;
    }

    public void setIdPreguntaQuiz(int idPreguntaQuiz) {
        this.idPreguntaQuiz = idPreguntaQuiz;
    }

    public Microleccion getMicroleccion() {
        return microleccion;
    }

    public void setMicroleccion(Microleccion microleccion) {
        this.microleccion = microleccion;
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

    public Integer getEstadoPreguntaQuiz() {
        return estadoPreguntaQuiz;
    }

    public void setEstadoPreguntaQuiz(Integer estadoPreguntaQuiz) {
        this.estadoPreguntaQuiz = estadoPreguntaQuiz;
    }
}
