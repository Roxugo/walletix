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

    // JPQL: ¿el usuario ya ve una categoría activa con ese nombre y tipo?
    // "Ve" = las predeterminadas + las suyas. idExcluido es la propia categoría al editarla (0 al crear).
    @Query("SELECT COUNT(categoria) > 0 FROM Categoria categoria " +
            "WHERE categoria.estadoCategoria = 1 " +
            "AND LOWER(TRIM(categoria.nombreCategoria)) = LOWER(TRIM(:nombre)) " +
            "AND LOWER(categoria.tipoCategoria) = LOWER(:tipo) " +
            "AND (categoria.predeterminadoCategoria = true OR categoria.usuario.idUsuario = :idUsuario) " +
            "AND categoria.idCategoria <> :idExcluido")
    boolean existeNombreVisible(@Param("nombre") String nombre, @Param("tipo") String tipo,
                                @Param("idUsuario") int idUsuario, @Param("idExcluido") int idExcluido);

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Categoria categoria SET categoria.estadoCategoria = 0 WHERE categoria.usuario.idUsuario = :idUsuario AND categoria.predeterminadoCategoria = false")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);
}
