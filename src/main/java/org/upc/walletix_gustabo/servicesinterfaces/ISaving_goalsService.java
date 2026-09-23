package org.upc.walletix_gustabo.servicesinterfaces;

import org.upc.walletix_gustabo.entity.Saving_goals;

import java.util.List;
import java.util.Optional;

public interface ISaving_goalsService {
    public List<Saving_goals> list();
    public Saving_goals insert(Saving_goals savingGoals);
    public Optional<Saving_goals> listId(int id);
    public void update(Saving_goals sg);
    public void delete(int id);
}
