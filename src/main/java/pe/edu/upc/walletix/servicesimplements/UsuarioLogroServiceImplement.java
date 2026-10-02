package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.UsuarioLogro;
import pe.edu.upc.walletix.repositories.IUsuarioLogroRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioLogroService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioLogroServiceImplement implements IUsuarioLogroService {

    @Autowired
    private IUsuarioLogroRepository hR;

    @Override
    public List<UsuarioLogro> list() {
        return hR.findAll();
    }

    @Override
    public UsuarioLogro insert(UsuarioLogro usac) {
        return hR.save(usac);
    }

    @Override
    public Optional<UsuarioLogro> listId(int id) {
        return hR.findById(id);
    }

    @Override
    public void update(UsuarioLogro ua) {
        hR.save(ua);
    }

    @Override
    public void delete(int id) {
        hR.deleteById(id);
    }
}
