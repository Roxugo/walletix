package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    public List<Usuario> list();
    public Usuario insert(Usuario usuario);
    public Optional<Usuario> listId(int id);
    public void update(Usuario usuario);
    public void delete(int id);
    public boolean existeCorreo(String correo);
    public boolean existeTelefono(int telefono);
    public Optional<Usuario> buscarPorCorreo(String correo);
}
