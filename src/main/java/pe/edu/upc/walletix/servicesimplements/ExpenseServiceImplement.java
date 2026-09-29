package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Expense;
import pe.edu.upc.walletix.repositories.IExpenseRepository;
import pe.edu.upc.walletix.servicesinterfaces.IExpenseService;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImplement implements IExpenseService {

    @Autowired
    private IExpenseRepository eR;

    @Override
    public List<Expense> list() {
        return eR.findAll();
    }

    @Override
    public Expense insert(Expense e) {
        return eR.save(e);
    }

    @Override
    public Optional<Expense> listId(int id) {
        return eR.findById(id);
    }

    @Override
    public void update(Expense e) {
        eR.save(e);
    }

    @Override
    public void delete(int id) {
        eR.deleteById(id);
    }
}
