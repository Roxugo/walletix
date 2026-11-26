package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Notificacion;

import java.util.List;
import java.util.Optional;

public interface INotificacionService {
    public List<Notificacion> listar();
    public Notificacion registrar(Notificacion notificacion);
    // US25: notificación automática (alerta, logro, reto). Solo se guarda si el usuario acepta ese tipo;
    // devuelve vacío si la desactivó. Las que crea un ADMIN a mano usan registrar() y no se filtran.
    public java.util.Optional<Notificacion> registrarAutomatica(Notificacion notificacion);
    public Optional<Notificacion> buscarPorId(int id);
    public void actualizar(Notificacion notificacion);
    public void eliminar(int id);
    public List<Notificacion> buscarNoLeidasPorUsuario(int idUsuario);
    public int contarNoLeidasPorUsuario(int idUsuario);
    // Marca como leídas todas las notificaciones activas del usuario; devuelve cuántas cambió
    public int marcarTodasComoLeidas(int idUsuario);
}
