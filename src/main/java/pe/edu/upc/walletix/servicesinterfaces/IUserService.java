package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    public List<User> list();
    public User insert(User usu);
    public Optional<User> listId(int id);
    public void update(User u);
    public void delete(int id);
}
