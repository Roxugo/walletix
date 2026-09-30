package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.UserQuizAttempts;

import java.util.List;
import java.util.Optional;

public interface IUserQuizAttemptService {
    public List<UserQuizAttempts> list();
    public UserQuizAttempts insert(UserQuizAttempts a);
    public Optional<UserQuizAttempts> listId(int id);
    public void update(UserQuizAttempts a);
    public void delete(int id);
    public List<Object[]> progresoPorUsuario(int idUser);
}
