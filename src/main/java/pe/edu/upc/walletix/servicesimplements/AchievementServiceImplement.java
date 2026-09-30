package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Achievement;
import pe.edu.upc.walletix.repositories.IAchievementRepository;
import pe.edu.upc.walletix.repositories.IUserRepository;
import pe.edu.upc.walletix.servicesinterfaces.IAchievementService;

import java.util.List;
import java.util.Optional;

@Service
public class AchievementServiceImplement implements IAchievementService {
    @Autowired
    private IAchievementRepository aR;

    @Override
    public List<Achievement> list() {
        return aR.findAll();
    }

    @Override
    public Achievement insert(Achievement logr) {
        return aR.save(logr);
    }

    @Override
    public Optional<Achievement> listId(int id) {
        return aR.findById(id);
    }

    @Override
    public void update(Achievement l) {
        aR.save(l);
    }

    @Override
    public void delete(int id) {
        aR.deleteById(id);
    }
}
