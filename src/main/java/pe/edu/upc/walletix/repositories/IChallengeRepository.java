package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Challenges;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IChallengeRepository extends JpaRepository<Challenges, Integer> {
    // Native: retos vigentes en una fecha
    @NativeQuery("SELECT * FROM challenges " +
            "WHERE start_date_challenge <= :fecha AND end_date_challenge >= :fecha " +
            "ORDER BY end_date_challenge")
    List<Challenges> buscarVigentes(@Param("fecha") LocalDate fecha);

    // Native: cantidad de usuarios inscritos por reto (columnas: id, título, total)
    @NativeQuery("SELECT c.id_challenge, c.title_challenge, COUNT(uc.id_user_challenge) " +
            "FROM challenges c LEFT JOIN user_challenges uc ON c.id_challenge = uc.id_challenge " +
            "GROUP BY c.id_challenge, c.title_challenge " +
            "ORDER BY c.id_challenge")
    List<Object[]> cantidadUsuariosPorReto();
}