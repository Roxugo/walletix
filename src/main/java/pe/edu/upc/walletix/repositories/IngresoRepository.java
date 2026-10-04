package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}
