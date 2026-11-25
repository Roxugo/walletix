package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.ProgresoDesafio;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IProgresoDesafioRepository extends JpaRepository<ProgresoDesafio, Integer> {

    // Días distintos en que el usuario registró progreso en algún reto (para calcular su racha)
    @Query("SELECT DISTINCT progreso.fechaProgreso FROM ProgresoDesafio progreso " +
            "WHERE progreso.usuarioDesafio.usuario.idUsuario = :idUsuario ORDER BY progreso.fechaProgreso DESC")
    List<LocalDate> fechasConProgreso(@Param("idUsuario") int idUsuario);
}
