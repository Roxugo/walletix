package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.MetaAhorro;

import java.util.List;

@Repository
public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Integer> {
    List<MetaAhorro> findAllByEstado(int estado);
    java.util.Optional<MetaAhorro> findByIdMetaAhorroAndEstado(int idMetaAhorro, int estado);

    @Query(value = """
            SELECT *
            FROM metas_Ahorro
            WHERE idUsuario = :idUsuario
              AND estado = 1
            """, nativeQuery = true)
    List<MetaAhorro> listarPorUsuario(
            @Param("idUsuario") int idUsuario
    );
}
