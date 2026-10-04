package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.PreguntaQuiz;

import java.util.List;
import java.util.Optional;

public interface IPreguntaQuizService {
    public List<PreguntaQuiz> list();
    public PreguntaQuiz insert(PreguntaQuiz preguntaQuiz);
    public Optional<PreguntaQuiz> listId(int id);
    public void update(PreguntaQuiz preguntaQuiz);
    public void delete(int id);
    public List<PreguntaQuiz> listarPorMicroleccion(int idMicroleccion);
}
