package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Microlessons;

import java.util.List;

@Repository
public interface IMicrolessonRepository extends JpaRepository<Microlessons, Integer> {

    // JPQL: consejos y microlecciones de una categoría (US31), sin importar mayúsculas
    @Query("SELECT m FROM Microlessons m WHERE LOWER(m.categoryMicrolesson) = LOWER(:categoria)")
    List<Microlessons> buscarPorCategoria(@Param("categoria") String categoria);
}
