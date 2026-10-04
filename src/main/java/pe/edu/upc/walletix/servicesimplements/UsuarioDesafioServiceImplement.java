package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.UsuarioDesafio;
import pe.edu.upc.walletix.repositories.IUsuarioDesafioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioDesafioService;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDesafioServiceImplement implements IUsuarioDesafioService {

    @Autowired
    private IUsuarioDesafioRepository usuarioDesafioRepository;

    @Override
    public List<UsuarioDesafio> listar() {
        return usuarioDesafioRepository.findByEstado(1);
    }

    @Override
    public UsuarioDesafio registrar(UsuarioDesafio usuarioDesafio) {
        return usuarioDesafioRepository.save(usuarioDesafio);
    }

    @Override
    public Optional<UsuarioDesafio> buscarPorId(int id) {
        return usuarioDesafioRepository.findByIdUsuarioDesafioAndEstado(id, 1);
    }

    @Override
    public void actualizar(UsuarioDesafio usuarioDesafio) {
        usuarioDesafioRepository.save(usuarioDesafio);
    }

    @Override
    public void eliminar(int id) {
        Optional<UsuarioDesafio> usuarioDesafioEncontrado = usuarioDesafioRepository.findByIdUsuarioDesafioAndEstado(id, 1);
        if (usuarioDesafioEncontrado.isPresent()) {
            usuarioDesafioEncontrado.get().setEstado(0); // Borrado lógico
            usuarioDesafioRepository.save(usuarioDesafioEncontrado.get());
        }
    }

    @Override
    public List<UsuarioDesafio> buscarPorUsuarioYEstadoDesafio(int idUsuario, String estadoDesafio) {
        return usuarioDesafioRepository.buscarPorUsuarioYEstadoDesafio(idUsuario, estadoDesafio);
    }

    @Override
    public int contarUsuariosPorDesafio(int idDesafio) {
        return usuarioDesafioRepository.contarUsuariosPorDesafio(idDesafio);
    }
}
