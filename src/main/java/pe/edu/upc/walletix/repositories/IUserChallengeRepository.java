package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.UserChallenges;

import java.util.List;

@Repository
public interface IUserChallengeRepository extends JpaRepository<UserChallenges, Integer> {
    // Native: retos de un usuario según su estado (activo, completado, fallido)
    @NativeQuery("SELECT * FROM user_challenges " +
            "WHERE id_user = :idUser AND status_user_challenge = :status")
    List<UserChallenges> buscarPorUsuarioYEstado(@Param("idUser") int idUser, @Param("status") String status);
}