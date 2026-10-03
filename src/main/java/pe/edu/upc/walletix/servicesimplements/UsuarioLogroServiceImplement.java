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
    private IUsuarioLogroRepository usuariologroRepository;

    @Override
    public List<UsuarioLogro> list() {
        return usuariologroRepository.findByEstadoUsuarioLogroTrue();
    }

    @Override
    public UsuarioLogro insert(UsuarioLogro usuariologro) {
        return usuariologroRepository.save(usuariologro);
    }

    @Override
    public Optional<UsuarioLogro> listId(int id) {
        return usuariologroRepository.findById(id);
    }

    @Override
    public void update(UsuarioLogro usuariologro) {
        usuariologroRepository.save(usuariologro);
    }

    @Override
    public void delete(int id) {
        Optional<UsuarioLogro> opt = usuariologroRepository.findById(id);
        if (opt.isPresent()) {
            UsuarioLogro ul = opt.get();
            ul.setEstadoUsuarioLogro(false); // Inactivar
            usuariologroRepository.save(ul);  // Guardar cambio
        }
    }

    @Override
    public List<String[]> cantidadLogrosPorUsuario() {
        return usuariologroRepository.cantidadLogrosPorUsuario();
    }

    @Override
    public List<String[]> logrosMasObtenidos() {
        return usuariologroRepository.logrosMasObtenidos();
    }
}
