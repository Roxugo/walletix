package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Presupuesto;


import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
    List<Presupuesto> findAllByEstado(int estado);
    java.util.Optional<Presupuesto> findByIdPresupuestoAndEstado(int idPresupuesto, int estado);

    // Un solo presupuesto activo por usuario, categoría, mes y año.
    // IdPresupuestoNot: al editar se ignora el mismo presupuesto (al registrar se pasa 0, que no existe)
    boolean existsByUsuarioIdUsuarioAndCategoriaIdCategoriaAndMesAndAnioAndEstadoAndIdPresupuestoNot(
            int idUsuario, int idCategoria, int mes, int anio, int estado, int idPresupuesto);

    // US15: presupuesto activo de un usuario para una categoría en un mes y año (Spring arma la consulta por el nombre)
    java.util.Optional<Presupuesto> findFirstByUsuarioIdUsuarioAndCategoriaIdCategoriaAndMesAndAnioAndEstado(
            int idUsuario, int idCategoria, int mes, int anio, int estado);

    @Query(value = """
            SELECT *
            FROM presupuesto
            WHERE idUsuario = :idUsuario
              AND estado = 1
              AND mes = :mes
              AND anio = :anio
            """, nativeQuery = true)
    List<Presupuesto> listarPorUsuarioYPeriodo(
            @Param("idUsuario") int idUsuario,
            @Param("mes") int mes,
            @Param("anio") int anio
    );

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Presupuesto presupuesto SET presupuesto.estado = 0 WHERE presupuesto.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);

    // Para no eliminar una categoría que todavía tiene presupuestos activos
    boolean existsByCategoriaIdCategoriaAndEstado(int idCategoria, int estado);
}
