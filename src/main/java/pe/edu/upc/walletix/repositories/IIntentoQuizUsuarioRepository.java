package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.IntentoQuizUsuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface IIntentoQuizUsuarioRepository extends JpaRepository<IntentoQuizUsuario, Integer> {

    // Solo los intentos activos (borrado lógico)
    List<IntentoQuizUsuario> findByEstadoIntentoQuizUsuario(Integer estadoIntentoQuizUsuario);

    Optional<IntentoQuizUsuario> findByIdIntentoQuizUsuarioAndEstadoIntentoQuizUsuario(int idIntentoQuizUsuario, Integer estadoIntentoQuizUsuario);

    // Query nativo: progreso de aprendizaje del usuario, una fila por microlección activa
    // Columnas: id de la microlección, título, cantidad de intentos, mejor puntaje, si aprobó alguna vez
    @Query(value = "SELECT microleccion.id_microleccion, microleccion.titulo_microleccion, " +
            "COUNT(intento.id_intento_quiz_usuario), MAX(intento.puntaje_intento_quiz_usuario), " +
            "BOOL_OR(intento.aprobado_intento_quiz_usuario) " +
            "FROM intento_quiz_usuario intento " +
            "JOIN microleccion microleccion ON microleccion.id_microleccion = intento.id_microleccion " +
            "WHERE intento.id_usuario = :idUsuario " +
            "AND intento.estado_intento_quiz_usuario = 1 " +
            "AND microleccion.estado_microleccion = 1 " +
            "GROUP BY microleccion.id_microleccion, microleccion.titulo_microleccion " +
            "ORDER BY microleccion.id_microleccion", nativeQuery = true)
    List<Object[]> progresoPorUsuario(@Param("idUsuario") int idUsuario);
}
