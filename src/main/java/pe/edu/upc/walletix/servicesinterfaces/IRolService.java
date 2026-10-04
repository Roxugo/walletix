package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Rol;

import java.util.List;

public interface IRolService {
    public List<Rol> list();
    public Rol insert(Rol rol);
    public boolean existeRol(int idUsuario, String rol);
}
