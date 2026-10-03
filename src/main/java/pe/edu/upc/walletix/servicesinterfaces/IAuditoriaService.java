package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Auditoria;

import java.util.List;
import java.util.Optional;

public interface IAuditoriaService {
    public List<Auditoria> list();
    public Auditoria insert(Auditoria auditoria);
    public Optional<Auditoria> listId(int id);
    public void update(Auditoria auditoria);
    public void delete(int id);
}
