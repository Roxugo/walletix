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
    public Optional<Auditoria> listId(int id) {
        return auditoriaRepository.findById(id);
    }
}
