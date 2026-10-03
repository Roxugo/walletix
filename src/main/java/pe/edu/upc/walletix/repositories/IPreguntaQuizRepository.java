package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.PreguntaQuiz;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPreguntaQuizRepository extends JpaRepository<PreguntaQuiz, Integer> {

    // Solo las preguntas activas (borrado lógico)
    List<PreguntaQuiz> findByEstadoPreguntaQuizTrue();

    Optional<PreguntaQuiz> findByIdPreguntaQuizAndEstadoPreguntaQuizTrue(int idPreguntaQuiz);

    // Preguntas activas del quiz de una microlección (Spring arma la consulta por el nombre del método)
    List<PreguntaQuiz> findByMicroleccionIdMicroleccionAndEstadoPreguntaQuizTrue(int idMicroleccion);
}
