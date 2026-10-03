package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Notifications;
import pe.edu.upc.walletix.repositories.INotificationRepository;
import pe.edu.upc.walletix.servicesinterfaces.INotificationService;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImplement implements INotificationService {

    @Autowired
    private INotificationRepository nR;

    @Override
    public List<Notifications> list() {
        return nR.findAll();
    }

    @Override
    public Notifications insert(Notifications n) {
        return nR.save(n);
    }

    @Override
    public Optional<Notifications> listId(int id) {
        return nR.findById(id);
    }

    @Override
    public void update(Notifications n) {
        nR.save(n);
    }

    @Override
    public void delete(int id) {
        nR.deleteById(id);
    }
}