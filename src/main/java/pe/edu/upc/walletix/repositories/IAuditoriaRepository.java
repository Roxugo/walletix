package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Auditoria;

import java.util.List;

@Repository
public interface IAuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    // Listar auditorías activas por estado numérico (1: Activo, 0: Inactivo)
    List<Auditoria> findByEstado(int estado);

    // Listar auditorías donde un usuario específico fue el creador
    List<Auditoria> findByUsuarioRegistroIdUsuario(int idUsuario);
}
