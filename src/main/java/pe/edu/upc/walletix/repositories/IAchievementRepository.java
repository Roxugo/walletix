package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upc.walletix.entities.Achievement;

public interface IAchievementRepository extends JpaRepository<Achievement, Integer> {
}
