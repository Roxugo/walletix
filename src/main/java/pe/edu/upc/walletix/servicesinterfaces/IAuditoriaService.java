package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Auditoria;

import java.util.List;
import java.util.Optional;

// La auditoría es de solo lectura: sus registros se crean y actualizan solos desde UsuarioServiceImplement
public interface IAuditoriaService {
    public List<Auditoria> list();
    public Optional<Auditoria> listId(int id);
}
