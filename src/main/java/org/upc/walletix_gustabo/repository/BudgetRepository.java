package org.upc.walletix_gustabo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.upc.walletix_gustabo.entity.Budgets;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budgets, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_budgets
            WHERE id_user = :idUser
              AND month = :mes
              AND year = :anio
            """, nativeQuery = true)
    List<Budgets> listarPorUsuarioYPeriodo(
            @Param("idUser") int idUser,
            @Param("mes") int mes,
            @Param("anio") int anio
    );
}
