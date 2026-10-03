package pe.edu.upc.walletix.servicesinterfaces;

import java.util.List;
import java.util.Optional;

public interface IIncomesService {
    public List<Incomes> list();
    public Incomes insert(Incomes incomes);
    public Optional<Incomes> listId(int id);
    public void update(Incomes i);
    public void delete(int id);
}
