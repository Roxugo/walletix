package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Rol;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    public List<Rol> list();
    public Rol insert(Rol rol);
    public Optional<Rol> listId(int id);
    public void update(Rol rol);
    public void delete(int id);
    public Optional<Rol> buscarPorUsuarioYRol(int idUsuario, String rol);
    public long contarRolesActivosDeUsuario(int idUsuario);
    public long contarUsuariosActivosConRol(String rol);
}
