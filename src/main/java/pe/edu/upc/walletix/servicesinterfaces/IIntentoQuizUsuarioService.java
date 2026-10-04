package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.IntentoQuizUsuario;

import java.util.List;
import java.util.Optional;

public interface IIntentoQuizUsuarioService {
    public List<IntentoQuizUsuario> list();
    public IntentoQuizUsuario insert(IntentoQuizUsuario intentoQuizUsuario);
    public Optional<IntentoQuizUsuario> listId(int id);
    public void update(IntentoQuizUsuario intentoQuizUsuario);
    public void delete(int id);
    public List<Object[]> progresoPorUsuario(int idUsuario);
}
