package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Budgets;
import pe.edu.upc.walletix.repositories.BudgetRepository;
import pe.edu.upc.walletix.servicesinterfaces.IBudgetsService;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetServiceImplement implements IBudgetsService {
    @Autowired
    private BudgetRepository bR;

    @Override
    public List<Budgets> list() {
        return bR.findAll();
    }

    @Override
    public Budgets insert(Budgets budgets) {
        return bR.save(budgets);
    }

    @Override
    public Optional<Budgets> listId(int id) {
        return bR.findById(id);
    }

    @Override
    public void update(Budgets b) {
        bR.save(b);
    }

    @Override
    public void delete(int id) {
        bR.deleteById(id);
    }
}
