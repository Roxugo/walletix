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
}
