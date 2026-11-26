package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Notificacion;
import pe.edu.upc.walletix.repositories.INotificacionRepository;
import pe.edu.upc.walletix.servicesinterfaces.INotificacionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionServiceImplement implements INotificacionService {

    @Autowired
    private INotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> listar() {
        // De la más reciente a la más antigua (por id: el id crece con cada notificación nueva)
        return notificacionRepository.findByEstadoOrderByIdNotificacionDesc(1);
    }

    @Override
    public Optional<Notificacion> registrarAutomatica(Notificacion notificacion) {
        if (notificacion.getUsuario() != null && !notificacion.getUsuario().aceptaNotificacion(notificacion.getTipo())) {
            return Optional.empty();
        }
        return Optional.of(registrar(notificacion));
    }

    @Override
    public Notificacion registrar(Notificacion notificacion) {
        // Toda notificación nueva lleva la fecha y hora en que se generó
        if (notificacion.getFechaNotificacion() == null) {
            notificacion.setFechaNotificacion(LocalDateTime.now());
        }
        return notificacionRepository.save(notificacion);
    }

    @Override
    public int marcarTodasComoLeidas(int idUsuario) {
        return notificacionRepository.marcarTodasComoLeidas(idUsuario);
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
