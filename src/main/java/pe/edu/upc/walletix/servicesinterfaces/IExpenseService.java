package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IExpenseService {
    public List<Expense> list();
    public Expense insert(Expense e);
    public Optional<Expense> listId(int id);
    public void update(Expense e);
    public void delete(int id);
    public List<Expense> buscarPorUsuarioYRango(int userId, LocalDate desde, LocalDate hasta);
}
