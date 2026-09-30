package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.UserQuizAttempts;

import java.util.List;

@Repository
public interface IUserQuizAttemptRepository extends JpaRepository<UserQuizAttempts, Integer> {

    // Query nativo: progreso de aprendizaje del usuario, una fila por microlección
    // Columnas: id de la microlección, título, cantidad de intentos, mejor nota, si aprobó alguna vez
    @Query(value = "SELECT m.id_microlesson, m.title_microlesson, " +
            "COUNT(a.id_user_quiz_attempt), MAX(a.score_user_quiz_attempt), BOOL_OR(a.is_passed_user_quiz_attempt) " +
            "FROM user_quiz_attempts a " +
            "JOIN microlessons m ON m.id_microlesson = a.id_microlesson " +
            "WHERE a.id_user = :idUser " +
            "GROUP BY m.id_microlesson, m.title_microlesson " +
            "ORDER BY m.id_microlesson", nativeQuery = true)
    List<Object[]> progresoPorUsuario(@Param("idUser") int idUser);
}
