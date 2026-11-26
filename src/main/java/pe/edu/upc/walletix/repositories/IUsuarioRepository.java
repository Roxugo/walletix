package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
    List<Usuario> findByEstadoUsuario(int estadoUsuario);
    Optional<Usuario> findByIdUsuarioAndEstadoUsuario(int idUsuario, int estadoUsuario);

    // Login: el correo funciona como nombre de usuario (sin distinguir mayúsculas)
    Optional<Usuario> findByCorreoUsuarioIgnoreCaseAndEstadoUsuario(String correoUsuario, int estadoUsuario);

    boolean existsByCorreoUsuarioIgnoreCase(String correoUsuario);

    // El teléfono tampoco se puede repetir entre cuentas
    boolean existsByTelefonoUsuario(int telefonoUsuario);

    // Saldo: suma el cambio (negativo = resta) en UN SOLO UPDATE y solo si no queda negativo.
    // Si llegan dos gastos a la vez, la base bloquea la fila: el segundo espera y vuelve a evaluar
    // la condición con el saldo ya descontado. Devuelve 1 si se aplicó y 0 si no alcanzaba.
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Usuario u set u.saldoActualUsuario = coalesce(u.saldoActualUsuario, 0) + :cambio "
            + "where u.idUsuario = :id and coalesce(u.saldoActualUsuario, 0) + :cambio >= 0")
    int sumarSaldoSiAlcanza(@Param("id") int idUsuario, @Param("cambio") BigDecimal cambio);

    // Saldo guardado en la base (no el que quedó en memoria al inicio de la petición)
    @Query("select coalesce(u.saldoActualUsuario, 0) from Usuario u where u.idUsuario = :id")
    BigDecimal saldoGuardado(@Param("id") int idUsuario);

    // Puntos: misma idea; nunca bajan de 0
    @Transactional
    @Modifying(flushAutomatically = true)
    @Query("update Usuario u set u.puntosGamificacionUsuario = case when u.puntosGamificacionUsuario + :cambio < 0 then 0 "
            + "else u.puntosGamificacionUsuario + :cambio end where u.idUsuario = :id")
    int sumarPuntos(@Param("id") int idUsuario, @Param("cambio") int cambio);

    @Query("select u.puntosGamificacionUsuario from Usuario u where u.idUsuario = :id")
    int puntosGuardados(@Param("id") int idUsuario);
}
