package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Comerciante;

import java.util.List;
import java.util.Optional;

public interface IComercianteService {
    public List<Comerciante> list();
    public Comerciante insert(Comerciante comerciante);
    public Optional<Comerciante> listId(int id);
    public void update(Comerciante comerciante);
    public void delete(int id);
}
