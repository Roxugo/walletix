package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.ProgresoDesafio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IProgresoDesafioRepository extends JpaRepository<ProgresoDesafio, Integer> {

    // Días distintos en que el usuario registró progreso en algún reto (para calcular su racha)
    // (solo de participaciones activas: si se elimina la participación, sus días ya no cuentan)
    @Query("SELECT DISTINCT progreso.fechaProgreso FROM ProgresoDesafio progreso " +
            "WHERE progreso.usuarioDesafio.usuario.idUsuario = :idUsuario AND progreso.usuarioDesafio.estado = 1 " +
            "ORDER BY progreso.fechaProgreso DESC")
    List<LocalDate> fechasConProgreso(@Param("idUsuario") int idUsuario);

    // El progreso de un día concreto (para quitarlo si un gasto de ese día rompe el reto)
    Optional<ProgresoDesafio> findFirstByUsuarioDesafioIdUsuarioDesafioAndFechaProgreso(int idUsuarioDesafio, LocalDate fechaProgreso);

    // ¿Ya ganó hoy los XP del progreso diario en algún reto? (incluye retos abandonados: así no se reinicia para volver a ganarlos)
    boolean existsByUsuarioDesafioUsuarioIdUsuarioAndFechaProgresoAndXpOtorgadoGreaterThan(int idUsuario, LocalDate fechaProgreso, Integer xp);

    // El último día con progreso que le queda a la participación
    Optional<ProgresoDesafio> findFirstByUsuarioDesafioIdUsuarioDesafioOrderByFechaProgresoDesc(int idUsuarioDesafio);
}
