package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.UsuarioDesafio;

import java.util.List;
import java.util.Optional;

public interface IUsuarioDesafioService {
    public List<UsuarioDesafio> listar();
    public UsuarioDesafio registrar(UsuarioDesafio usuarioDesafio);
    public Optional<UsuarioDesafio> buscarPorId(int id);
    public void actualizar(UsuarioDesafio usuarioDesafio);
    public void eliminar(int id);
    public List<UsuarioDesafio> buscarPorUsuarioYEstadoDesafio(int idUsuario, String estadoDesafio);
    public int contarUsuariosPorDesafio(int idDesafio);
}
