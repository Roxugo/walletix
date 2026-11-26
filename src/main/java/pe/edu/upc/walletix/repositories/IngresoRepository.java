package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Ingreso;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngresoRepository extends JpaRepository<Ingreso, Integer> {
    List<Ingreso> findAllByEstado(int estado);
    java.util.Optional<Ingreso> findByIdIngresoAndEstado(int idIngreso, int estado);

    @Query(value = """
            SELECT *
            FROM ingreso
            WHERE idUsuario = :idUsuario
              AND estado = 1
              AND fecha >= :fechaInicio
              AND fecha < :fechaFin
            """, nativeQuery = true)
    List<Ingreso> listarPorUsuarioYPeriodo(
            @Param("idUsuario") int idUsuario,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    // US39 (reporte PDF): ingresos activos de un usuario entre dos fechas, en orden
    @Query("SELECT ingreso FROM Ingreso ingreso WHERE ingreso.usuario.idUsuario = :idUsuario " +
            "AND ingreso.fecha BETWEEN :fechaInicio AND :fechaFin AND ingreso.estado = 1 ORDER BY ingreso.fecha")
    List<Ingreso> buscarPorUsuarioYRango(@Param("idUsuario") int idUsuario,
                                        @Param("fechaInicio") LocalDate fechaInicio,
                                        @Param("fechaFin") LocalDate fechaFin);

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Ingreso ingreso SET ingreso.estado = 0 WHERE ingreso.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);

    // Para no eliminar una categoría que todavía tiene ingresos activos
    boolean existsByCategoriaIdCategoriaAndEstado(int idCategoria, int estado);

    // Logros: cantidad de ingresos del usuario
    long countByUsuarioIdUsuarioAndEstado(int idUsuario, int estado);
}
