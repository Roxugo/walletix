package pe.edu.upc.walletix.servicesinterfaces;

import pe.edu.upc.walletix.entities.Category;

import java.util.List;
import java.util.Optional;

public interface ICategoryService {
    public List<Category> list();
    public Category insert(Category c);
    public Optional<Category> listId(int id);
    public void update(Category c);
    public void delete(int id);
    public List<Category> buscarPorTipo(String type);
}
