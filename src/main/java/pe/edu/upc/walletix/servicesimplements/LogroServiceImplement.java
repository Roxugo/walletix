package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Logro;
import pe.edu.upc.walletix.repositories.ILogroRepository;
import pe.edu.upc.walletix.servicesinterfaces.ILogroService;

import java.util.List;
import java.util.Optional;

@Service
public class LogroServiceImplement implements ILogroService {
    @Autowired
    private ILogroRepository aR;

    @Override
    public List<Logro> list() {
        return aR.findAll();
    }

    @Override
    public Logro insert(Logro logr) {
        return aR.save(logr);
    }

    @Override
    public Optional<Logro> listId(int id) {
        return aR.findById(id);
    }

    @Override
    public void update(Logro l) {
        aR.save(l);
    }

    @Override
    public void delete(int id) {
        aR.deleteById(id);
    }
}
