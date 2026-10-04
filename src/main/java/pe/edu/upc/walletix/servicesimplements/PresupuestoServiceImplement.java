package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Presupuesto;
import pe.edu.upc.walletix.repositories.PresupuestoRepository;
import pe.edu.upc.walletix.servicesinterfaces.PresupuestoServiceInterface;


import java.util.List;
import java.util.Optional;

@Service
public class PresupuestoServiceImplement implements PresupuestoServiceInterface {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Override
    public List<Presupuesto> listar() {
        return presupuestoRepository.findAllByEstado(1);
    }

    @Override
    public Presupuesto registrar(Presupuesto presupuestos) {
        return presupuestoRepository.save(presupuestos);
    }

    @Override
    public Optional<Presupuesto> buscarPorId(int id) {
        return presupuestoRepository.findByIdPresupuestoAndEstado(id, 1);
    }

    @Override
    public void actualizar(Presupuesto b) {
        presupuestoRepository.save(b);
    }

    @Override
    public void eliminar(int id) {
        presupuestoRepository.findByIdPresupuestoAndEstado(id, 1).ifPresent(presupuesto -> {
            presupuesto.setEstado(0);
            presupuestoRepository.save(presupuesto);
        });
    }
}
