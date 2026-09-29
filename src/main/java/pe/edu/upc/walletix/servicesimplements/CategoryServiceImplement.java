package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Category;
import pe.edu.upc.walletix.repositories.ICategoryRepository;
import pe.edu.upc.walletix.servicesinterfaces.ICategoryService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImplement implements ICategoryService {

    @Autowired
    private ICategoryRepository cR;

    @Override
    public List<Category> list() {
        return cR.findAll();
    }

    @Override
    public Category insert(Category c) {
        return cR.save(c);
    }

    @Override
    public Optional<Category> listId(int id) {
        return cR.findById(id);
    }

    @Override
    public void update(Category c) {
        cR.save(c);
    }

    @Override
    public void delete(int id) {
        cR.deleteById(id);
    }
}
