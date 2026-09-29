package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.SavingGoals;

import java.util.List;
import java.util.Optional;

public interface ISavingGoalService {
    public List<SavingGoals> list();
    public SavingGoals insert(SavingGoals savingGoals);
    public Optional<SavingGoals> listId(int id);
    public void update(SavingGoals sg);
    public void delete(int id);
}
