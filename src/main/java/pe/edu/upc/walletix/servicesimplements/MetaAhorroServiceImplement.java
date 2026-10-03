package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.MetaAhorro;
import pe.edu.upc.walletix.repositories.MetaAhorroRepository;
import pe.edu.upc.walletix.servicesinterfaces.MetaAhorroServiceInterface;

import java.util.List;
import java.util.Optional;

@Service
public class MetaAhorroServiceImplement implements MetaAhorroServiceInterface {

    @Autowired
    private MetaAhorroRepository metaAhorroRepository;

    @Override
    public List<MetaAhorro> listar() {
        return metaAhorroRepository.findAllByEstado(1);
    }

    @Override
    public MetaAhorro registrar(MetaAhorro savingGoals) {
        return metaAhorroRepository.save(savingGoals);
    }

    @Override
    public Optional<MetaAhorro> buscarPorId(int id) {
        return metaAhorroRepository.findByIdMetaAhorroAndEstado(id, 1);
    }

    @Override
    public void actualizar(MetaAhorro sg) {
        metaAhorroRepository.save(sg);
    }

    @Override
    public void eliminar(int id) {
        metaAhorroRepository.findByIdMetaAhorroAndEstado(id, 1).ifPresent(metaAhorro -> {
            metaAhorro.setEstado(0);
            metaAhorroRepository.save(metaAhorro);
        });
    }
}
