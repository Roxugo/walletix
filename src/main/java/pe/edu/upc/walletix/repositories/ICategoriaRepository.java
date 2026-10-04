package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Categoria;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Solo las categorías activas (borrado lógico)
    List<Categoria> findByEstadoCategoria(Integer estadoCategoria);

    Optional<Categoria> findByIdCategoriaAndEstadoCategoria(int idCategoria, Integer estadoCategoria);

    // JPQL: categorías activas de un tipo (gasto o ingreso), sin importar mayúsculas
    @Query("SELECT categoria FROM Categoria categoria " +
            "WHERE LOWER(categoria.tipoCategoria) = LOWER(:tipo) " +
            "AND categoria.estadoCategoria = 1")
    List<Categoria> buscarPorTipo(@Param("tipo") String tipo);
}
