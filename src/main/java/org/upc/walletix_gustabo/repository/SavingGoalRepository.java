package org.upc.walletix_gustabo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.upc.walletix_gustabo.entity.Saving_goals;

import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<Saving_goals, Integer> {
    @Query(value = """
            SELECT *
            FROM tm_saving_goals
            WHERE id_user = :idUser
            """, nativeQuery = true)
    List<Saving_goals> listarPorUsuario(
            @Param("idUser") int idUser
    );
}
