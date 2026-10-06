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
    // true si todavía tiene preguntas o intentos de quiz activos (no se puede eliminar)
    public boolean tieneRegistrosActivos(int id);
}
