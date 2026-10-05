package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Rol;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer> {
    List<Rol> findByEstadoRol(int estadoRol);

    Optional<Rol> findByIdRolAndEstadoRol(int idRol, int estadoRol);

    // Busca la fila aunque esté eliminada (estado 0), para reactivarla en vez de duplicarla
    Optional<Rol> findByUsuarioIdUsuarioAndRol(int idUsuario, String rol);

    // Cuántos roles activos le quedan a un usuario
    long countByUsuarioIdUsuarioAndEstadoRol(int idUsuario, int estadoRol);

    // Cuántos usuarios activos tienen un rol activo (ej. cuántos ADMIN quedan)
    long countByRolAndEstadoRolAndUsuarioEstadoUsuario(String rol, int estadoRol, int estadoUsuario);
}
