package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.UsuarioLogro;

import java.util.List;

@Repository
public interface IUsuarioLogroRepository extends JpaRepository<UsuarioLogro, Integer> {
    List<UsuarioLogro> findByEstadoUsuarioLogroTrue();
    @Query(value = "SELECT u.nombre_usuario, COUNT(ul.id_usuario_logro) " +
            "FROM usuario u " +
            "INNER JOIN usuario_logro ul ON u.id_usuario = ul.id_usuario " +
            "WHERE ul.estado_usuario_logro = true " +
            "GROUP BY u.id_usuario, u.nombre_usuario " +
            "ORDER BY COUNT(ul.id_usuario_logro) DESC", nativeQuery = true)
    List<String[]> cantidadLogrosPorUsuario();

    @Query(value = "SELECT l.nombre_logro, COUNT(ul.id_usuario_logro) " +
            "FROM logro l " +
            "LEFT JOIN usuario_logro ul ON l.id_logro = ul.id_logro AND ul.estado_usuario_logro = true " +
            "GROUP BY l.id_logro, l.nombre_logro " +
            "ORDER BY COUNT(ul.id_usuario_logro) DESC", nativeQuery = true)
    List<String[]> logrosMasObtenidos();
}
