package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.walletix.entities.Logro;

import java.util.List;
import java.util.Optional;

public interface ILogroRepository extends JpaRepository<Logro, Integer> {
    List<Logro> findByEstadoLogroTrue();
    Optional<Logro> findByIdLogroAndEstadoLogroTrue(int idLogro);
}
