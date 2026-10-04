package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Auditoria;
import pe.edu.upc.walletix.repositories.IAuditoriaRepository;
import pe.edu.upc.walletix.servicesinterfaces.IAuditoriaService;

import java.util.List;
import java.util.Optional;

@Service
public class AuditoriaServiceImplement implements IAuditoriaService {

    @Autowired
    private IAuditoriaRepository auditoriaRepository;

    @Override
    public List<Auditoria> list() {
        // En auditoría devolvemos el historial completo para ver todas las fases (incluso eliminados)
        return auditoriaRepository.findAll();
    }

    @Override
    public Auditoria insert(Auditoria auditoria) {
        return auditoriaRepository.save(auditoria);
    }

    @Override
    public Optional<Auditoria> listId(int id) {
        return auditoriaRepository.findById(id);
    }

    @Override
    public void update(Auditoria auditoria) {
        auditoriaRepository.save(auditoria);
    }

    @Override
    public void delete(int id) {
        Optional<Auditoria> opt = auditoriaRepository.findById(id);
        if (opt.isPresent()) {
            Auditoria auditoria = opt.get();
            auditoria.setEstado(0); // Baja lógica con 0
            auditoriaRepository.save(auditoria);
        }
    }
}
