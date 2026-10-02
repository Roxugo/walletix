package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.walletix.entities.Logro;

import java.util.List;

public interface ILogroRepository extends JpaRepository<Logro, Integer> {
    List<Logro> findByEstadoLogroTrue();
}
