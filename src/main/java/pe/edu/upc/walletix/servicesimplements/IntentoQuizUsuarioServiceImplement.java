package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.IntentoQuizUsuario;
import pe.edu.upc.walletix.repositories.IIntentoQuizUsuarioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IIntentoQuizUsuarioService;

import java.util.List;
import java.util.Optional;

@Service
public class IntentoQuizUsuarioServiceImplement implements IIntentoQuizUsuarioService {

    @Autowired
    private IIntentoQuizUsuarioRepository intentoQuizUsuarioRepository;

    @Override
    public List<IntentoQuizUsuario> list() {
        return intentoQuizUsuarioRepository.findByEstadoIntentoQuizUsuario(1);
    }

    @Override
    public IntentoQuizUsuario insert(IntentoQuizUsuario intentoQuizUsuario) {
        // Todo registro nuevo empieza activo
        intentoQuizUsuario.setEstadoIntentoQuizUsuario(1);
        return intentoQuizUsuarioRepository.save(intentoQuizUsuario);
    }

    @Override
    public Optional<IntentoQuizUsuario> listId(int id) {
        return intentoQuizUsuarioRepository.findByIdIntentoQuizUsuarioAndEstadoIntentoQuizUsuario(id, 1);
    }

    @Override
    public void update(IntentoQuizUsuario intentoQuizUsuario) {
        intentoQuizUsuarioRepository.save(intentoQuizUsuario);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<IntentoQuizUsuario> intentoQuizUsuario = intentoQuizUsuarioRepository.findByIdIntentoQuizUsuarioAndEstadoIntentoQuizUsuario(id, 1);
        if (intentoQuizUsuario.isPresent()) {
            intentoQuizUsuario.get().setEstadoIntentoQuizUsuario(0);
            intentoQuizUsuarioRepository.save(intentoQuizUsuario.get());
        }
    }

    @Override
    public List<Object[]> progresoPorUsuario(int idUsuario) {
        return intentoQuizUsuarioRepository.progresoPorUsuario(idUsuario);
    }
}
