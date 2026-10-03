package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.UserChallenges;
import pe.edu.upc.walletix.repositories.IUserChallengeRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUserChallengeService;

import java.util.List;
import java.util.Optional;

@Service
public class UserChallengeServiceImplement implements IUserChallengeService {

    @Autowired
    private IUserChallengeRepository ucR;

    @Override
    public List<UserChallenges> list() {
        return ucR.findAll();
    }

    @Override
    public UserChallenges insert(UserChallenges uc) {
        return ucR.save(uc);
    }

    @Override
    public Optional<UserChallenges> listId(int id) {
        return ucR.findById(id);
    }

    @Override
    public void update(UserChallenges uc) {
        ucR.save(uc);
    }

    @Override
    public void delete(int id) {
        ucR.deleteById(id);
    }
}