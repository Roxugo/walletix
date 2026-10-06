package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Logro;

import java.util.List;
import java.util.Optional;

public interface ILogroService {
    public List<Logro> list();
    public Logro insert(Logro logro);
    public Optional<Logro> listId(int id);
    public void update(Logro logro);
    public void delete(int id);
    // true si todavía tiene usuarios que lo obtuvieron activos (no se puede eliminar)
    public boolean tieneRegistrosActivos(int id);
}
