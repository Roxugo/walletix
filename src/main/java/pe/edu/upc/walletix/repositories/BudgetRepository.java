package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Budgets;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budgets, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_budgets
            WHERE id_user = :idUser
              AND month = :month
              AND year = :year
            """, nativeQuery = true)
    List<Budgets> listarPorUsuarioYPeriodo(
            @Param("idUser") int idUser,
            @Param("mes") int month,
            @Param("anio") int year
    );
}
