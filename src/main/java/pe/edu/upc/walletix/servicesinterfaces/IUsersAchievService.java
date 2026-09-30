package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Achievement;
import pe.edu.upc.walletix.entities.UsersAchiev;

import java.util.List;
import java.util.Optional;

public interface IUsersAchievService {
    public List<UsersAchiev> list();
    public UsersAchiev insert(UsersAchiev usac);
    public Optional<UsersAchiev> listId(int id);
    public void update(UsersAchiev ua);
    public void delete(int id);
}
