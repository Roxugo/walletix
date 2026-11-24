package pe.edu.upc.walletix.servicesinterfaces;



import pe.edu.upc.walletix.entities.Presupuesto;

import java.util.List;
import java.util.Optional;

public interface PresupuestoServiceInterface {
    public List<Presupuesto> listar();
    public Presupuesto registrar(Presupuesto presupuestos);
    public Optional<Presupuesto> buscarPorId(int id);
    public void actualizar(Presupuesto b);
    public void eliminar(int id);
    // true si el usuario ya tiene otro presupuesto activo para esa categoría en ese mes y año
    public boolean existeDuplicado(int idUsuario, int idCategoria, int mes, int anio, int idPresupuestoExcluido);
}
