package pe.edu.upc.walletix.servicesinterfaces;

import java.util.List;
import java.util.Optional;

public interface ISavingGoalService {
    public List<SavingGoal> list();
    public SavingGoal insert(SavingGoal savingGoals);
    public Optional<SavingGoal> listId(int id);
    public void update(SavingGoal sg);
    public void delete(int id);
}
