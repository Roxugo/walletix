package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Achievement;
import pe.edu.upc.walletix.entities.Users;

import java.util.List;
import java.util.Optional;

public interface IAchievementService {
    public List<Achievement> list();
    public Achievement insert(Achievement logr);
    public Optional<Achievement> listId(int id);
    public void update(Achievement l);
    public void delete(int id);
}
