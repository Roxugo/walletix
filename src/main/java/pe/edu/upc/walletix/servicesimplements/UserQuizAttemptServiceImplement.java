package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.UserQuizAttempts;
import pe.edu.upc.walletix.repositories.IUserQuizAttemptRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUserQuizAttemptService;

import java.util.List;
import java.util.Optional;

@Service
public class UserQuizAttemptServiceImplement implements IUserQuizAttemptService {

    @Autowired
    private IUserQuizAttemptRepository aR;

    @Override
    public List<UserQuizAttempts> list() {
        return aR.findAll();
    }

    @Override
    public UserQuizAttempts insert(UserQuizAttempts a) {
        return aR.save(a);
    }

    @Override
    public Optional<UserQuizAttempts> listId(int id) {
        return aR.findById(id);
    }

    @Override
    public void update(UserQuizAttempts a) {
        aR.save(a);
    }

    @Override
    public void delete(int id) {
        aR.deleteById(id);
    }

    @Override
    public List<Object[]> progresoPorUsuario(int idUser) {
        return aR.progresoPorUsuario(idUser);
    }
}
