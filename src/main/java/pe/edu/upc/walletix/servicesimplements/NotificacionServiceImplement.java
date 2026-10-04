package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.repositories.INotificacionRepository;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;

import java.util.List;
import java.util.Optional;

@Service
public class NotificacionServiceImplement implements INotificacionService {

    @Autowired
    private INotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> listar() {
        return notificacionRepository.findByEstado(1);
    }

    @Override
    public Notificacion registrar(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    @Override
    public Optional<Notificacion> buscarPorId(int id) {
        return notificacionRepository.findByIdNotificacionAndEstado(id, 1);
    }

    @Override
    public void actualizar(Notificacion notificacion) {
        notificacionRepository.save(notificacion);
    }

    @Override
    public void eliminar(int id) {
        Optional<Notificacion> notificacionEncontrado = notificacionRepository.findByIdNotificacionAndEstado(id, 1);
        if (notificacionEncontrado.isPresent()) {
            notificacionEncontrado.get().setEstado(0); // Borrado lógico
            notificacionRepository.save(notificacionEncontrado.get());
        }
    }

    @Override
    public List<Notificacion> buscarNoLeidasPorUsuario(int idUsuario) {
        return notificacionRepository.buscarNoLeidasPorUsuario(idUsuario);
    }

    @Override
    public int contarNoLeidasPorUsuario(int idUsuario) {
        return notificacionRepository.contarNoLeidasPorUsuario(idUsuario);
    }
}
