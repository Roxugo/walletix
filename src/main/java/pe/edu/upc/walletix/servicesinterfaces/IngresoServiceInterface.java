package pe.edu.upc.walletix.servicesinterfaces;


import pe.edu.upc.walletix.entities.Ingreso;

import java.util.List;
import java.util.Optional;

public interface IngresoServiceInterface {
    public List<Ingreso> listar();
    public Ingreso registrar(Ingreso ingresos);
    public Optional<Ingreso> buscarPorId(int id);
    // Igual que buscarPorId, pero bloquea el ingreso para editarlo o eliminarlo (requiere una transacción)
    Optional<Ingreso> buscarParaModificar(int id);
    public void actualizar(Ingreso i);
    public void eliminar(int id);
}
