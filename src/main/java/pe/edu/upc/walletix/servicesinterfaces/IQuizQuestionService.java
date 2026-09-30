package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.QuizQuestions;

import java.util.List;
import java.util.Optional;

public interface IQuizQuestionService {
    public List<QuizQuestions> list();
    public QuizQuestions insert(QuizQuestions q);
    public Optional<QuizQuestions> listId(int id);
    public void update(QuizQuestions q);
    public void delete(int id);
    public List<QuizQuestions> listByMicrolesson(int idMicrolesson);
}
