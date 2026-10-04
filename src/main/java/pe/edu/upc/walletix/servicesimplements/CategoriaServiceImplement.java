package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.repositories.ICategoriaRepository;
import pe.edu.upc.walletix.servicesinterfaces.ICategoriaService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImplement implements ICategoriaService {

    @Autowired
    private ICategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> list() {
        return categoriaRepository.findByEstadoCategoria(1);
    }

    @Override
    public Categoria insert(Categoria categoria) {
        // Todo registro nuevo empieza activo
        categoria.setEstadoCategoria(1);
        return categoriaRepository.save(categoria);
    }

    @Override
    public Optional<Categoria> listId(int id) {
        return categoriaRepository.findByIdCategoriaAndEstadoCategoria(id, 1);
    }

    @Override
    public void update(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Categoria> categoria = categoriaRepository.findByIdCategoriaAndEstadoCategoria(id, 1);
        if (categoria.isPresent()) {
            categoria.get().setEstadoCategoria(0);
            categoriaRepository.save(categoria.get());
        }
    }

    @Override
    public List<Categoria> buscarPorTipo(String tipo) {
        return categoriaRepository.buscarPorTipo(tipo);
    }
}
