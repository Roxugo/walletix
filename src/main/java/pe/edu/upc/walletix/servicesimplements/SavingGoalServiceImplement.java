package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.SavingGoals;
import pe.edu.upc.walletix.repositories.SavingGoalRepository;
import pe.edu.upc.walletix.servicesinterfaces.ISavingGoalService;

import java.util.List;
import java.util.Optional;

@Service
public class SavingGoalServiceImplement implements ISavingGoalService {
    @Autowired
    private SavingGoalRepository sgR;

    @Override
    public List<SavingGoals> list() {
        return sgR.findAll();
    }

    @Override
    public SavingGoals insert(SavingGoals savingGoals) {
        return sgR.save(savingGoals);
    }

    @Override
    public Optional<SavingGoals> listId(int id) {
        return sgR.findById(id);
    }

    @Override
    public void update(SavingGoals sg) {
        sgR.save(sg);
    }

    @Override
    public void delete(int id) {
        sgR.deleteById(id);
    }
}
