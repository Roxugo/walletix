package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Notifications;

import java.util.List;
import java.util.Optional;

public interface INotificationService {
    public List<Notifications> list();
    public Notifications insert(Notifications n);
    public Optional<Notifications> listId(int id);
    public void update(Notifications n);
    public void delete(int id);
}