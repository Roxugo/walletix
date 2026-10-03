package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.servicesinterfaces.ISavingGoalService;

import java.util.List;
import java.util.Optional;

@Service
public class SavingGoalServiceImplement implements ISavingGoalService {
    @Autowired
    private SavingGoalRepository sgR;

    @Override
    public List<SavingGoal> list() {
        return sgR.findAll();
    }

    @Override
    public SavingGoal insert(SavingGoal savingGoals) {
        return sgR.save(savingGoals);
    }

    @Override
    public Optional<SavingGoal> listId(int id) {
        return sgR.findById(id);
    }

    @Override
    public void update(SavingGoal sg) {
        sgR.save(sg);
    }

    @Override
    public void delete(int id) {
        sgR.deleteById(id);
    }
}
