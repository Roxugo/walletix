package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.repositories.IncomeRepository;
import pe.edu.upc.walletix.servicesinterfaces.IIncomesService;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeServiceImplement implements IIncomesService {
    @Autowired
    private IncomeRepository iR;

    @Override
    public List<Incomes> list() {
        return iR.findAll();
    }

    @Override
    public Incomes insert(Incomes incomes) {
        return iR.save(incomes);
    }

    @Override
    public Optional<Incomes> listId(int id) {
        return iR.findById(id);
    }

    @Override
    public void update(Incomes i) {
        iR.save(i);
    }

    @Override
    public void delete(int id) {
        iR.deleteById(id);
    }
}
