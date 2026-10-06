package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.walletix.entities.Notificacion;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByEstado(int estado);

    Optional<Notificacion> findByIdNotificacionAndEstado(int idNotificacion, int estado);

    // Query 1: notificaciones no leídas de un usuario
    @NativeQuery("SELECT * FROM notificacion " +
            "WHERE id_usuario = :idUsuario AND estado_notificacion = false AND estado = 1 " +
            "ORDER BY id_notificacion DESC")
    List<Notificacion> buscarNoLeidasPorUsuario(@Param("idUsuario") int idUsuario);

    // Query 2: cantidad de notificaciones no leídas de un usuario
    @NativeQuery("SELECT COUNT(*) FROM notificacion " +
            "WHERE id_usuario = :idUsuario AND estado_notificacion = false AND estado = 1")
    int contarNoLeidasPorUsuario(@Param("idUsuario") int idUsuario);

    // Borrado lógico en cascada: al eliminar un usuario se dan de baja sus registros
    @Transactional
    @Modifying
    @Query("UPDATE Notificacion notificacion SET notificacion.estado = 0 WHERE notificacion.usuario.idUsuario = :idUsuario")
    void darDeBajaPorUsuario(@Param("idUsuario") int idUsuario);
}
