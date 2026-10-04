package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Logro;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILogroRepository extends JpaRepository<Logro, Integer> {
    List<Logro> findByEstadoLogro(int estadoLogro);
    Optional<Logro> findByIdLogroAndEstadoLogro(int idLogro, int estadoLogro);
}
