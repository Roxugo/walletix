package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.UsuarioLogro;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioLogroRepository extends JpaRepository<UsuarioLogro, Integer> {
    List<UsuarioLogro> findByEstadoUsuarioLogro(int estadoUsuarioLogro);
    Optional<UsuarioLogro> findByIdUsuarioLogroAndEstadoUsuarioLogro(int idUsuarioLogro, int estadoUsuarioLogro);

    @Query(value = "SELECT u.nombre_usuario, COUNT(ul.id_usuario_logro) " +
            "FROM usuario u " +
            "INNER JOIN usuario_logro ul ON u.id_usuario = ul.id_usuario " +
            "WHERE ul.estado_usuario_logro = 1 " +
            "GROUP BY u.id_usuario, u.nombre_usuario " +
            "ORDER BY COUNT(ul.id_usuario_logro) DESC", nativeQuery = true)
    List<String[]> cantidadLogrosPorUsuario();

    @Query(value = "SELECT l.nombre_logro, COUNT(ul.id_usuario_logro) " +
            "FROM logro l " +
            "LEFT JOIN usuario_logro ul ON l.id_logro = ul.id_logro AND ul.estado_usuario_logro = 1 " +
            "GROUP BY l.id_logro, l.nombre_logro " +
            "ORDER BY COUNT(ul.id_usuario_logro) DESC", nativeQuery = true)
    List<String[]> logrosMasObtenidos();

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE UsuarioLogro usuarioLogro SET usuarioLogro.estadoUsuarioLogro = 0 WHERE usuarioLogro.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);

    // Para no eliminar un logro que todavía tienen usuarios
    boolean existsByLogroIdLogroAndEstadoUsuarioLogro(int idLogro, int estadoUsuarioLogro);
}
