package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Users;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    public List<Users> list();
    public Users insert(Users usu);
    public Optional<Users> listId(int id);
    public void update(Users u);
    public void delete(int id);
}
