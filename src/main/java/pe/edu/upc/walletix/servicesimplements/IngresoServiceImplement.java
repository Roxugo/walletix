package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Ingreso;
import pe.edu.upc.walletix.repositories.IngresoRepository;
import pe.edu.upc.walletix.servicesinterfaces.IngresoServiceInterface;


import java.util.List;
import java.util.Optional;

@Service
public class IngresoServiceImplement implements IngresoServiceInterface {

    @Autowired
    private IngresoRepository ingresoRepository;

    @Override
    public List<Ingreso> listar() {
        return ingresoRepository.findAllByEstado(1);
    }

    @Override
    public Ingreso registrar(Ingreso ingresos) {
        return ingresoRepository.save(ingresos);
    }

    @Override
    public Optional<Ingreso> buscarPorId(int id) {
        return ingresoRepository.findByIdIngresoAndEstado(id, 1);
    }

    @Override
    public void actualizar(Ingreso i) {
        ingresoRepository.save(i);
    }

    @Override
    public void eliminar(int id) {
        ingresoRepository.findByIdIngresoAndEstado(id, 1).ifPresent(ingreso -> {
            ingreso.setEstado(0);
            ingresoRepository.save(ingreso);
        });
    }
}
