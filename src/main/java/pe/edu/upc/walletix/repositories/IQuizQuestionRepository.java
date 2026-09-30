package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.QuizQuestions;

import java.util.List;

@Repository
public interface IQuizQuestionRepository extends JpaRepository<QuizQuestions, Integer> {

    // Preguntas del quiz de una microlección (Spring arma la consulta por el nombre del método)
    List<QuizQuestions> findByMicrolessonIdMicrolesson(int idMicrolesson);
}
