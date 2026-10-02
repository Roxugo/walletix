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
    private ILogroRepository logroRepository;

    @Override
    public List<Logro> list() {
        return logroRepository.findAll();
    }

    @Override
    public Logro insert(Logro logr) {
        return logroRepository.save(logr);
    }

    @Override
    public Optional<Logro> listId(int id) {
        return logroRepository.findById(id);
    }

    @Override
    public void update(Logro l) {
        logroRepository.save(l);
    }

    @Override
    public void delete(int id) {
        logroRepository.deleteById(id);
    }
}
