package org.upc.walletix_gustabo.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upc.walletix_gustabo.entity.Budgets;
import org.upc.walletix_gustabo.repository.BudgetRepository;
import org.upc.walletix_gustabo.servicesinterfaces.IBudgetsService;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetsServiceImplement implements IBudgetsService {

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
