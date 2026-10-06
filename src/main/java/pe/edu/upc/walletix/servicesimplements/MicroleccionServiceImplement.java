package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.repositories.IMicroleccionRepository;
import pe.edu.upc.walletix.repositories.IPreguntaQuizRepository;
import pe.edu.upc.walletix.repositories.IIntentoQuizUsuarioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;

import java.util.List;
import java.util.Optional;

@Service
public class MicroleccionServiceImplement implements IMicroleccionService {

    @Autowired
    private IMicroleccionRepository microleccionRepository;
    @Autowired
    private IPreguntaQuizRepository preguntaQuizRepository;
    @Autowired
    private IIntentoQuizUsuarioRepository intentoQuizUsuarioRepository;

    @Override
    public List<Microleccion> list() {
        return microleccionRepository.findByEstadoMicroleccion(1);
    }

    @Override
    public Microleccion insert(Microleccion microleccion) {
        // Todo registro nuevo empieza activo
        microleccion.setEstadoMicroleccion(1);
        return microleccionRepository.save(microleccion);
    }

    @Override
    public Optional<Microleccion> listId(int id) {
        return microleccionRepository.findByIdMicroleccionAndEstadoMicroleccion(id, 1);
    }

    @Override
    public void update(Microleccion microleccion) {
        microleccionRepository.save(microleccion);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Microleccion> microleccion = microleccionRepository.findByIdMicroleccionAndEstadoMicroleccion(id, 1);
        if (microleccion.isPresent()) {
            microleccion.get().setEstadoMicroleccion(0);
            microleccionRepository.save(microleccion.get());
        }
    }

    @Override
    public List<Microleccion> buscarPorCategoria(String categoria) {
        return microleccionRepository.buscarPorCategoria(categoria);
    }

    @Override
    public boolean tieneRegistrosActivos(int id) {
        return preguntaQuizRepository.existsByMicroleccionIdMicroleccionAndEstadoPreguntaQuiz(id, 1)
                || intentoQuizUsuarioRepository.existsByMicroleccionIdMicroleccionAndEstadoIntentoQuizUsuario(id, 1);
    }
}
