package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Gasto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IGastoService {
    public List<Gasto> list();
    public Gasto insert(Gasto gasto);
    public Optional<Gasto> listId(int id);
    public void update(Gasto gasto);
    public void delete(int id);
    public List<Gasto> buscarPorUsuarioYRango(int idUsuario, LocalDate fechaInicio, LocalDate fechaFin);
}
