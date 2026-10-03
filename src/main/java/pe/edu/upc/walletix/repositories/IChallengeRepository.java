package pe.edu.upc.walletix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.upc.walletix.entities.Challenges;

@Repository
public interface IChallengeRepository extends JpaRepository<Challenges, Integer> {
}