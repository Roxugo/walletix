package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.UsersAchiev;
import pe.edu.upc.walletix.repositories.IUserRepository;
import pe.edu.upc.walletix.repositories.IUsersAchievRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUsersAchievService;

import java.util.List;
import java.util.Optional;

@Service
public class UsersAchievServiceImplement implements IUsersAchievService {

    @Autowired
    private IUsersAchievRepository hR;

    @Override
    public List<UsersAchiev> list() {
        return hR.findAll();
    }

    @Override
    public UsersAchiev insert(UsersAchiev usac) {
        return hR.save(usac);
    }

    @Override
    public Optional<UsersAchiev> listId(int id) {
        return hR.findById(id);
    }

    @Override
    public void update(UsersAchiev ua) {
        hR.save(ua);
    }

    @Override
    public void delete(int id) {
        hR.deleteById(id);
    }
}
