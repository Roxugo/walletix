package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Microlessons;
import pe.edu.upc.walletix.repositories.IMicrolessonRepository;
import pe.edu.upc.walletix.servicesinterfaces.IMicrolessonService;

import java.util.List;
import java.util.Optional;

@Service
public class MicrolessonServiceImplement implements IMicrolessonService {

    @Autowired
    private IMicrolessonRepository mR;

    @Override
    public List<Microlessons> list() {
        return mR.findAll();
    }

    @Override
    public Microlessons insert(Microlessons m) {
        return mR.save(m);
    }

    @Override
    public Optional<Microlessons> listId(int id) {
        return mR.findById(id);
    }

    @Override
    public void update(Microlessons m) {
        mR.save(m);
    }

    @Override
    public void delete(int id) {
        mR.deleteById(id);
    }

    @Override
    public List<Microlessons> buscarPorCategoria(String categoria) {
        return mR.buscarPorCategoria(categoria);
    }
}
