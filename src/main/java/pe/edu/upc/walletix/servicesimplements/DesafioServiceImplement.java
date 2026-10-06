package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Desafio;
import pe.edu.upc.walletix.repositories.IDesafioRepository;
import pe.edu.upc.walletix.repositories.IUsuarioDesafioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IDesafioService;

import java.util.List;
import java.util.Optional;

@Service
public class DesafioServiceImplement implements IDesafioService {

    @Autowired
    private IDesafioRepository desafioRepository;
    @Autowired
    private IUsuarioDesafioRepository usuarioDesafioRepository;

    @Override
    public List<Desafio> listar() {
        return desafioRepository.findByEstado(1);
    }

    @Override
    public Desafio registrar(Desafio desafio) {
        return desafioRepository.save(desafio);
    }

    @Override
    public Optional<Desafio> buscarPorId(int id) {
        return desafioRepository.findByIdDesafioAndEstado(id, 1);
    }

    @Override
    public void actualizar(Desafio desafio) {
        desafioRepository.save(desafio);
    }

    @Override
    public void eliminar(int id) {
        Optional<Desafio> desafioEncontrado = desafioRepository.findByIdDesafioAndEstado(id, 1);
        if (desafioEncontrado.isPresent()) {
            desafioEncontrado.get().setEstado(0); // Borrado lógico
            desafioRepository.save(desafioEncontrado.get());
        }
    }

    @Override
    public List<Desafio> buscarVigentes() {
        return desafioRepository.buscarVigentes();
    }

    @Override
    public List<Desafio> buscarPorEdadMinima(int edad) {
        return desafioRepository.buscarPorEdadMinima(edad);
    }

    @Override
    public boolean tieneRegistrosActivos(int id) {
        return usuarioDesafioRepository.existsByDesafioIdDesafioAndEstado(id, 1);
    }
}
