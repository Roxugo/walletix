package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Users;
import pe.edu.upc.walletix.repositories.IUserRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplement implements IUserService {

    @Autowired
    private IUserRepository uR;

    @Override
    public List<Users> list() {
        return uR.findAll();
    }

    @Override
    public Users insert(Users usu) {
        return uR.save(usu);
    }

    @Override
    public Optional<Users> listId(int id) {
        return uR.findById(id);
    }

    @Override
    public void update(Users u) {
        uR.save(u);
    }

    @Override
    public void delete(int id) {
        uR.deleteById(id);
    }
}
