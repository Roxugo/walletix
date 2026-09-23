package org.upc.walletix_gustabo.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upc.walletix_gustabo.entity.Saving_goals;
import org.upc.walletix_gustabo.repository.SavingGoalRepository;
import org.upc.walletix_gustabo.servicesinterfaces.ISaving_goalsService;

import java.util.List;
import java.util.Optional;

@Service
public class SavingGoalServiceImplement implements ISaving_goalsService {

    @Autowired
    private SavingGoalRepository sgR;

    @Override
    public List<Saving_goals> list() {
        return sgR.findAll();
    }

    @Override
    public Saving_goals insert(Saving_goals savingGoals) {
        return sgR.save(savingGoals);
    }

    @Override
    public Optional<Saving_goals> listId(int id) {
        return sgR.findById(id);
    }

    @Override
    public void update(Saving_goals sg) {
        sgR.save(sg);
    }

    @Override
    public void delete(int id) {
        sgR.deleteById(id);
    }
}
