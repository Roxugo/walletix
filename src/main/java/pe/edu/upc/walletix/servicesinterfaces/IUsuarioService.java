package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    public List<Usuario> list();
    public Usuario insert(Usuario usu);
    public Optional<Usuario> listId(int id);
    public void update(Usuario u);
    public void delete(int id);
}
