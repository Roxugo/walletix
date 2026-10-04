package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Comerciante;
import pe.edu.upc.walletix.repositories.IComercianteRepository;
import pe.edu.upc.walletix.servicesinterfaces.IComercianteService;

import java.util.List;
import java.util.Optional;

@Service
public class ComercianteServiceImplement implements IComercianteService {

    @Autowired
    private IComercianteRepository comercianteRepository;

    @Override
    public List<Comerciante> list() {
        return comercianteRepository.findByEstadoComerciante(1);
    }

    @Override
    public Comerciante insert(Comerciante comerciante) {
        // Todo registro nuevo empieza activo
        comerciante.setEstadoComerciante(1);
        return comercianteRepository.save(comerciante);
    }

    @Override
    public Optional<Comerciante> listId(int id) {
        return comercianteRepository.findByIdComercianteAndEstadoComerciante(id, 1);
    }

    @Override
    public void update(Comerciante comerciante) {
        comercianteRepository.save(comerciante);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Comerciante> comerciante = comercianteRepository.findByIdComercianteAndEstadoComerciante(id, 1);
        if (comerciante.isPresent()) {
            comerciante.get().setEstadoComerciante(0);
            comercianteRepository.save(comerciante.get());
        }
    }
}
