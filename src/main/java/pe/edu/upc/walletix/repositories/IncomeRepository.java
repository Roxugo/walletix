package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Incomes;

import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Incomes, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_incomes
            WHERE id_user = :idUser
              AND MONTH(date) = :month
              AND YEAR(date) = :year
            """, nativeQuery = true)
    List<Incomes> listarPorUsuarioYPeriodo(
            @Param("idUser") int idUser,
            @Param("mes") int month,
            @Param("anio") int year
    );
}
