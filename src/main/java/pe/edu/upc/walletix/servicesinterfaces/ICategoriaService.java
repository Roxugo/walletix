package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Categoria;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {
    public List<Categoria> list();
    public Categoria insert(Categoria categoria);
    public Optional<Categoria> listId(int id);
    public void update(Categoria categoria);
    public void delete(int id);
    public List<Categoria> buscarPorTipo(String tipo);
    // true si todavía tiene comercios, gastos, ingresos o presupuestos activos (no se puede eliminar)
    public boolean tieneRegistrosActivos(int id);
}
