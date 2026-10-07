package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Usuario;

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
}
