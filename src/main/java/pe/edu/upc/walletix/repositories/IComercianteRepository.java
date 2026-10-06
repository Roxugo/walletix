package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
}
