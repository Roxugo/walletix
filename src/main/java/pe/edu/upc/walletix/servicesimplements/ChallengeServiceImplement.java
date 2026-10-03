package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Challenges;
import pe.edu.upc.walletix.repositories.IChallengeRepository;
import pe.edu.upc.walletix.servicesinterfaces.IChallengeService;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeServiceImplement implements IChallengeService {

    @Autowired
    private IChallengeRepository cR;

    @Override
    public List<Challenges> list() {
        return cR.findAll();
    }

    @Override
    public Challenges insert(Challenges c) {
        return cR.save(c);
    }

    @Override
    public Optional<Challenges> listId(int id) {
        return cR.findById(id);
    }

    @Override
    public void update(Challenges c) {
        cR.save(c);
    }

    @Override
    public void delete(int id) {
        cR.deleteById(id);
    }
}