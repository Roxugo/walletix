package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Microleccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMicroleccionRepository extends JpaRepository<Microleccion, Integer> {

    // Solo las microlecciones activas (borrado lógico)
    List<Microleccion> findByEstadoMicroleccionTrue();

    Optional<Microleccion> findByIdMicroleccionAndEstadoMicroleccionTrue(int idMicroleccion);

    // JPQL: consejos y microlecciones activos de una categoría (US31), sin importar mayúsculas
    @Query("SELECT microleccion FROM Microleccion microleccion " +
            "WHERE LOWER(microleccion.categoriaEducativaMicroleccion) = LOWER(:categoria) " +
            "AND microleccion.estadoMicroleccion = true")
    List<Microleccion> buscarPorCategoria(@Param("categoria") String categoria);
}
