package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Notifications;

import java.util.List;

@Repository
public interface INotificationRepository extends JpaRepository<Notifications, Integer> {
    // JPQL: notificaciones no leídas de un usuario
    @Query("SELECT n FROM Notifications n WHERE n.user.idUser = :idUser AND n.readNotification = false")
    List<Notifications> buscarNoLeidasPorUsuario(@Param("idUser") int idUser);

    // Native: cantidad de notificaciones de un usuario por tipo (columnas: tipo, total)
    @NativeQuery("SELECT type_notification, COUNT(id_notification) " +
            "FROM notifications WHERE id_user = :idUser " +
            "GROUP BY type_notification " +
            "ORDER BY type_notification")
    List<Object[]> cantidadPorTipo(@Param("idUser") int idUser);
}