package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Microleccion;

import java.util.List;
import java.util.Optional;

public interface IMicroleccionService {
    public List<Microleccion> list();
    public Microleccion insert(Microleccion microleccion);
    public Optional<Microleccion> listId(int id);
    public void update(Microleccion microleccion);
    public void delete(int id);
    public List<Microleccion> buscarPorCategoria(String categoria);
}
