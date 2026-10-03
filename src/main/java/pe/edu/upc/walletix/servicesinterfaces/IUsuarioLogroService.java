package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.UsuarioLogro;

import java.util.List;
import java.util.Optional;

public interface IUsuarioLogroService {
    public List<UsuarioLogro> list();
    public UsuarioLogro insert(UsuarioLogro usuariologro);
    public Optional<UsuarioLogro> listId(int id);
    public void update(UsuarioLogro usuariologro);
    public void delete(int id);
    List<String[]> cantidadLogrosPorUsuario();
    List<String[]> logrosMasObtenidos();
}
