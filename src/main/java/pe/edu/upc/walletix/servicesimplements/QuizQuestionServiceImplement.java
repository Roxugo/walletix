package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.QuizQuestions;
import pe.edu.upc.walletix.repositories.IQuizQuestionRepository;
import pe.edu.upc.walletix.servicesinterfaces.IQuizQuestionService;

import java.util.List;
import java.util.Optional;

@Service
public class QuizQuestionServiceImplement implements IQuizQuestionService {

    @Autowired
    private IQuizQuestionRepository qR;

    @Override
    public List<QuizQuestions> list() {
        return qR.findAll();
    }

    @Override
    public QuizQuestions insert(QuizQuestions q) {
        return qR.save(q);
    }

    @Override
    public Optional<QuizQuestions> listId(int id) {
        return qR.findById(id);
    }

    @Override
    public void update(QuizQuestions q) {
        qR.save(q);
    }

    @Override
    public void delete(int id) {
        qR.deleteById(id);
    }

    @Override
    public List<QuizQuestions> listByMicrolesson(int idMicrolesson) {
        return qR.findByMicrolessonIdMicrolesson(idMicrolesson);
    }
}
