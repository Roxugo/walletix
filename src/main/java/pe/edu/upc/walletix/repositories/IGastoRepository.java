package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Gasto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IGastoRepository extends JpaRepository<Gasto, Integer> {

    // Solo los gastos activos (borrado lógico)
    List<Gasto> findByEstadoGasto(Integer estadoGasto);

    Optional<Gasto> findByIdGastoAndEstadoGasto(int idGasto, Integer estadoGasto);

    // JPQL: gastos activos de un usuario entre dos fechas (US09, US18, US19)
    @Query("SELECT gasto FROM Gasto gasto " +
            "WHERE gasto.usuario.idUsuario = :idUsuario " +
            "AND gasto.fechaGasto BETWEEN :fechaInicio AND :fechaFin " +
            "AND gasto.estadoGasto = 1 " +
            "ORDER BY gasto.fechaGasto")
    List<Gasto> buscarPorUsuarioYRango(@Param("idUsuario") int idUsuario,
                                       @Param("fechaInicio") LocalDate fechaInicio,
                                       @Param("fechaFin") LocalDate fechaFin);

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Gasto gasto SET gasto.estadoGasto = 0 WHERE gasto.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);

    // Para no eliminar una categoría o un comercio que todavía tiene gastos activos
    boolean existsByCategoriaIdCategoriaAndEstadoGasto(int idCategoria, Integer estadoGasto);

    boolean existsByComercianteIdComercianteAndEstadoGasto(int idComerciante, Integer estadoGasto);
}
