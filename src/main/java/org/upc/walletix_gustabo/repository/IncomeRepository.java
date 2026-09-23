package org.upc.walletix_gustabo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.upc.walletix_gustabo.entity.Incomes;

import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Incomes, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_incomes
            WHERE id_user = :idUser
              AND MONTH(date) = :mes
              AND YEAR(date) = :anio
            """, nativeQuery = true)
    List<Incomes> listarPorUsuarioYPeriodo(
            @Param("idUser") int idUser,
            @Param("mes") int mes,
            @Param("anio") int anio
    );
}
