package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.UsuarioDesafio;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioDesafioRepository extends JpaRepository<UsuarioDesafio, Integer> {
    List<UsuarioDesafio> findByEstado(int estado);

    Optional<UsuarioDesafio> findByIdUsuarioDesafioAndEstado(int idUsuarioDesafio, int estado);

    // Query 1: desafíos de un usuario según el estado del desafío (ej. "EN_PROGRESO")
    @NativeQuery("SELECT * FROM usuario_desafio " +
            "WHERE id_usuario = :idUsuario AND estado_desafio = :estadoDesafio AND estado = 1 " +
            "ORDER BY porcentaje_progreso DESC")
    List<UsuarioDesafio> buscarPorUsuarioYEstadoDesafio(@Param("idUsuario") int idUsuario,
                                                        @Param("estadoDesafio") String estadoDesafio);

    // Query 2: cantidad de usuarios inscritos en un desafío
    @NativeQuery("SELECT COUNT(*) FROM usuario_desafio " +
            "WHERE id_desafio = :idDesafio AND estado = 1")
    int contarUsuariosPorDesafio(@Param("idDesafio") int idDesafio);
}