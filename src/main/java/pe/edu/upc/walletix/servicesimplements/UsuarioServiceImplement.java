package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.repositories.IUsuarioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImplement implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> list() {
        return usuarioRepository.findByEstadoUsuarioTrue();
    }

    @Override
    public Usuario insert(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> listId(int id) {
        return usuarioRepository.findByIdUsuarioAndEstadoUsuarioTrue(id);
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public void delete(int id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario usuario = opt.get();
            usuario.setEstadoUsuario(false); // Inactivar
            usuarioRepository.save(usuario); // Guardar cambio
        }
    }
}
