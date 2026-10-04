package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Notificacion;

import java.util.List;
import java.util.Optional;

public interface INotificacionService {
    public List<Notificacion> listar();
    public Notificacion registrar(Notificacion notificacion);
    public Optional<Notificacion> buscarPorId(int id);
    public void actualizar(Notificacion notificacion);
    public void eliminar(int id);
    public List<Notificacion> buscarNoLeidasPorUsuario(int idUsuario);
    public int contarNoLeidasPorUsuario(int idUsuario);
}
