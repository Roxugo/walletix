package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
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
}