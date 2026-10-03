package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.PreguntaQuiz;
import pe.edu.upc.walletix.repositories.IPreguntaQuizRepository;
import pe.edu.upc.walletix.servicesinterfaces.IPreguntaQuizService;

import java.util.List;
import java.util.Optional;

@Service
public class PreguntaQuizServiceImplement implements IPreguntaQuizService {

    @Autowired
    private IPreguntaQuizRepository preguntaQuizRepository;

    @Override
    public List<PreguntaQuiz> list() {
        return preguntaQuizRepository.findByEstadoPreguntaQuizTrue();
    }

    @Override
    public PreguntaQuiz insert(PreguntaQuiz preguntaQuiz) {
        // Todo registro nuevo empieza activo
        preguntaQuiz.setEstadoPreguntaQuiz(true);
        return preguntaQuizRepository.save(preguntaQuiz);
    }

    @Override
    public Optional<PreguntaQuiz> listId(int id) {
        return preguntaQuizRepository.findByIdPreguntaQuizAndEstadoPreguntaQuizTrue(id);
    }

    @Override
    public void update(PreguntaQuiz preguntaQuiz) {
        preguntaQuizRepository.save(preguntaQuiz);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<PreguntaQuiz> preguntaQuiz = preguntaQuizRepository.findByIdPreguntaQuizAndEstadoPreguntaQuizTrue(id);
        if (preguntaQuiz.isPresent()) {
            preguntaQuiz.get().setEstadoPreguntaQuiz(false);
            preguntaQuizRepository.save(preguntaQuiz.get());
        }
    }

    @Override
    public List<PreguntaQuiz> listarPorMicroleccion(int idMicroleccion) {
        return preguntaQuizRepository.findByMicroleccionIdMicroleccionAndEstadoPreguntaQuizTrue(idMicroleccion);
    }
}
