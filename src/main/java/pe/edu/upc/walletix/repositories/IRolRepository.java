package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Rol;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer> {
    // Roles activos de usuarios activos (un usuario eliminado ya no aparece)
    List<Rol> findByEstadoRolAndUsuarioEstadoUsuario(int estadoRol, int estadoUsuario);

    Optional<Rol> findByIdRolAndEstadoRolAndUsuarioEstadoUsuario(int idRol, int estadoRol, int estadoUsuario);

    // Busca la fila aunque esté dada de baja (estado 0), para reactivarla en vez de duplicarla
    Optional<Rol> findByUsuarioIdUsuarioAndRol(int idUsuario, String rol);

    // Roles activos de un usuario (en uso normal, siempre uno solo)
    List<Rol> findByUsuarioIdUsuarioAndEstadoRol(int idUsuario, int estadoRol);

    // Cuántos usuarios activos tienen un rol activo (ej. cuántos ADMIN quedan)
    long countByRolAndEstadoRolAndUsuarioEstadoUsuario(String rol, int estadoRol, int estadoUsuario);
}
