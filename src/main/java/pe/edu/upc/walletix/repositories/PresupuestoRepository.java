package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Presupuesto;


import java.util.List;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
    List<Presupuesto> findAllByEstado(int estado);
    java.util.Optional<Presupuesto> findByIdPresupuestoAndEstado(int idPresupuesto, int estado);

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
}
