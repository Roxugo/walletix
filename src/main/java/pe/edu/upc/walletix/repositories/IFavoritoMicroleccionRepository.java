package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.FavoritoMicroleccion;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoritoMicroleccionRepository extends JpaRepository<FavoritoMicroleccion, Integer> {
    // US32: los favoritos del usuario (quitar un favorito borra la fila: no es un dato que haya que conservar)
    List<FavoritoMicroleccion> findByUsuarioIdUsuario(int idUsuario);

    Optional<FavoritoMicroleccion> findByUsuarioIdUsuarioAndMicroleccionIdMicroleccion(int idUsuario, int idMicroleccion);

    // Al eliminar un usuario se borran sus favoritos (hijos antes que el padre)
    @Transactional
    @Modifying
    @Query("DELETE FROM FavoritoMicroleccion favorito WHERE favorito.usuario.idUsuario = :idUsuario")
    void eliminarPorUsuario(@Param("idUsuario") int idUsuario);
}
