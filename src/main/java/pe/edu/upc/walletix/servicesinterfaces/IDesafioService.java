package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Desafio;

import java.util.List;
import java.util.Optional;

public interface IDesafioService {
    public List<Desafio> listar();
    public Desafio registrar(Desafio desafio);
    public Optional<Desafio> buscarPorId(int id);
    public void actualizar(Desafio desafio);
    public void eliminar(int id);
    public List<Desafio> buscarVigentes();
    public List<Desafio> buscarPorEdadMinima(int edad);
    // true si todavía tiene participantes activos (no se puede eliminar)
    public boolean tieneRegistrosActivos(int id);
}
