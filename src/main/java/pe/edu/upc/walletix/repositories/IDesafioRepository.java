package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Desafio;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDesafioRepository extends JpaRepository<Desafio, Integer> {
    List<Desafio> findByEstado(int estado);

    Optional<Desafio> findByIdDesafioAndEstado(int idDesafio, int estado);

    // Query 1: desafíos vigentes (la fecha de hoy está dentro del rango)
    @NativeQuery("SELECT * FROM desafio " +
            "WHERE fecha_inicio <= CURRENT_DATE AND fecha_fin >= CURRENT_DATE AND estado = 1 " +
            "ORDER BY fecha_fin ASC")
    List<Desafio> buscarVigentes();

    // Query 2: desafíos a los que puede acceder un usuario según su edad
    @NativeQuery("SELECT * FROM desafio " +
            "WHERE edad_minima <= :edad AND estado = 1 " +
            "ORDER BY puntos_recompensa DESC")
    List<Desafio> buscarPorEdadMinima(@Param("edad") int edad);
}