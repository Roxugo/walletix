package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.UserChallenges;

import java.util.List;
import java.util.Optional;

public interface IUserChallengeService {
    public List<UserChallenges> list();
    public UserChallenges insert(UserChallenges uc);
    public Optional<UserChallenges> listId(int id);
    public void update(UserChallenges uc);
    public void delete(int id);
}