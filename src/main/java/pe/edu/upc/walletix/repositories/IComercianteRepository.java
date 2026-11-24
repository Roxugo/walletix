package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Comerciante;

import java.util.List;
import java.util.Optional;

@Repository
public interface IComercianteRepository extends JpaRepository<Comerciante, Integer> {

    // Solo los comercios activos (borrado lógico)
    List<Comerciante> findByEstadoComerciante(Integer estadoComerciante);

    Optional<Comerciante> findByIdComercianteAndEstadoComerciante(int idComerciante, Integer estadoComerciante);

    // Para no eliminar una categoría que todavía tiene comercios activos
    boolean existsByCategoriaIdCategoriaAndEstadoComerciante(int idCategoria, Integer estadoComerciante);

    // JPQL: ¿el usuario ya ve un comercio activo con ese nombre?
    // "Ve" = los predeterminados (sin dueño) + los suyos. LEFT JOIN para incluir los que no tienen dueño.
    // idExcluido es el propio comercio al editarlo (0 al crear).
    @Query("SELECT COUNT(comerciante) > 0 FROM Comerciante comerciante LEFT JOIN comerciante.usuario dueno " +
            "WHERE comerciante.estadoComerciante = 1 " +
            "AND LOWER(TRIM(comerciante.nombreComerciante)) = LOWER(TRIM(:nombre)) " +
            "AND (dueno IS NULL OR dueno.idUsuario = :idUsuario) " +
            "AND comerciante.idComerciante <> :idExcluido")
    boolean existeNombreVisible(@Param("nombre") String nombre, @Param("idUsuario") int idUsuario,
                                @Param("idExcluido") int idExcluido);

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus comercios propios
    // (los predeterminados no tienen dueño, así que no se tocan)
    @Transactional
    @Modifying
    @Query("UPDATE Comerciante comerciante SET comerciante.estadoComerciante = 0 WHERE comerciante.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);
}
