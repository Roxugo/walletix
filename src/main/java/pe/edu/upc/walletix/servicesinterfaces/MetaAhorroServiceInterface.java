package pe.edu.upc.walletix.servicesinterfaces;


import pe.edu.upc.walletix.entities.MetaAhorro;

import java.util.List;
import java.util.Optional;

public interface MetaAhorroServiceInterface {
    public List<MetaAhorro> listar();
    public MetaAhorro registrar(MetaAhorro metaAhorro);
    public Optional<MetaAhorro> buscarPorId(int id);
    public void actualizar(MetaAhorro metaAhorro);
    public void eliminar(int id);
}
