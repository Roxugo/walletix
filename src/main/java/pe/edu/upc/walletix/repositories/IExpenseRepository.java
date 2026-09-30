package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Expense;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IExpenseRepository extends JpaRepository<Expense, Integer> {
    @Query("SELECT e FROM Expense e WHERE e.userIdExpense = :userId AND e.dateExpense BETWEEN :desde AND :hasta")
    List<Expense> buscarPorUsuarioYRango(@Param("userId") int userId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
