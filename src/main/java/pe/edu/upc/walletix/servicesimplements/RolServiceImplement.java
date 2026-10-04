package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.repositories.IRolRepository;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;

import java.util.List;

@Service
public class RolServiceImplement implements IRolService {
    @Autowired
    private IRolRepository rolRepository;

    @Override
    public List<Rol> list() {
        return rolRepository.findAll();
    }

    @Override
    public Rol insert(Rol rol) {
        return rolRepository.save(rol);
    }

    @Override
    public boolean existeRol(int idUsuario, String rol) {
        return rolRepository.existsByUsuarioIdUsuarioAndRol(idUsuario, rol);
    }
}
