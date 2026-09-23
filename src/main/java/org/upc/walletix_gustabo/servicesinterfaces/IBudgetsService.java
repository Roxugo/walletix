package org.upc.walletix_gustabo.servicesinterfaces;

import org.upc.walletix_gustabo.entity.Budgets;

import java.util.List;
import java.util.Optional;

public interface IBudgetsService {
    public List<Budgets> list();
    public Budgets insert(Budgets budgets);
    public Optional<Budgets> listId(int id);
    public void update(Budgets b);
    public void delete(int id);
}
