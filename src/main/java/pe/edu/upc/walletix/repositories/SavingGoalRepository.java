package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_saving_goals
            WHERE id_user = :idUser
            """, nativeQuery = true)
    List<SavingGoal> listarPorUsuario(
            @Param("idUser") int idUser
    );
}
