package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Microlessons;

import java.util.List;
import java.util.Optional;

public interface IMicrolessonService {
    public List<Microlessons> list();
    public Microlessons insert(Microlessons m);
    public Optional<Microlessons> listId(int id);
    public void update(Microlessons m);
    public void delete(int id);
    public List<Microlessons> buscarPorCategoria(String categoria);
}
