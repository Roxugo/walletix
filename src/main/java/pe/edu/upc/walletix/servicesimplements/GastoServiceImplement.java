package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Gasto;
import pe.edu.upc.walletix.repositories.IGastoRepository;
import pe.edu.upc.walletix.servicesinterfaces.IGastoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GastoServiceImplement implements IGastoService {

    @Autowired
    private IGastoRepository gastoRepository;

    @Override
    public List<Gasto> list() {
        return gastoRepository.findByEstadoGasto(1);
    }

    @Override
    public Gasto insert(Gasto gasto) {
        // Todo registro nuevo empieza activo
        gasto.setEstadoGasto(1);
        return gastoRepository.save(gasto);
    }

    @Override
    public Optional<Gasto> listId(int id) {
        return gastoRepository.findByIdGastoAndEstadoGasto(id, 1);
    }

    @Override
    public void update(Gasto gasto) {
        gastoRepository.save(gasto);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Gasto> gasto = gastoRepository.findByIdGastoAndEstadoGasto(id, 1);
        if (gasto.isPresent()) {
            gasto.get().setEstadoGasto(0);
            gastoRepository.save(gasto.get());
        }
    }

    @Override
    public List<Gasto> buscarPorUsuarioYRango(int idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
        return gastoRepository.buscarPorUsuarioYRango(idUsuario, fechaInicio, fechaFin);
    }
}
