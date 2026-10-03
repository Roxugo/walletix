package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Challenges;

import java.util.List;
import java.util.Optional;

public interface IChallengeService {
    public List<Challenges> list();
    public Challenges insert(Challenges c);
    public Optional<Challenges> listId(int id);
    public void update(Challenges c);
    public void delete(int id);
}