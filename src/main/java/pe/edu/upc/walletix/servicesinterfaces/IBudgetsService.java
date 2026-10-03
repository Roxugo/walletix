package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Budgets;

import java.util.List;
import java.util.Optional;

public interface IBudgetsService {
    public List<Budgets> list();
    public Budgets insert(Budgets budgets);
    public Optional<Budgets> listId(int id);
    public void update(Budgets b);
    public void delete(int id);
}
