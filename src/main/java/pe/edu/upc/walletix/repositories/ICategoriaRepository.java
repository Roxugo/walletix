package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
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

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Categoria categoria SET categoria.estadoCategoria = 0 WHERE categoria.usuario.idUsuario = :idUsuario AND categoria.predeterminadoCategoria = false")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);
}
