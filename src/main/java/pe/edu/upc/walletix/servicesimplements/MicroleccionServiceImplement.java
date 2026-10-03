package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Microleccion;
import pe.edu.upc.walletix.repositories.IMicroleccionRepository;
import pe.edu.upc.walletix.servicesinterfaces.IMicroleccionService;

import java.util.List;
import java.util.Optional;

@Service
public class MicroleccionServiceImplement implements IMicroleccionService {

    @Autowired
    private IMicroleccionRepository microleccionRepository;

    @Override
    public List<Microleccion> list() {
        return microleccionRepository.findByEstadoMicroleccionTrue();
    }

    @Override
    public Microleccion insert(Microleccion microleccion) {
        // Todo registro nuevo empieza activo
        microleccion.setEstadoMicroleccion(true);
        return microleccionRepository.save(microleccion);
    }

    @Override
    public Optional<Microleccion> listId(int id) {
        return microleccionRepository.findByIdMicroleccionAndEstadoMicroleccionTrue(id);
    }

    @Override
    public void update(Microleccion microleccion) {
        microleccionRepository.save(microleccion);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Microleccion> microleccion = microleccionRepository.findByIdMicroleccionAndEstadoMicroleccionTrue(id);
        if (microleccion.isPresent()) {
            microleccion.get().setEstadoMicroleccion(false);
            microleccionRepository.save(microleccion.get());
        }
    }

    @Override
    public List<Microleccion> buscarPorCategoria(String categoria) {
        return microleccionRepository.buscarPorCategoria(categoria);
    }
}
