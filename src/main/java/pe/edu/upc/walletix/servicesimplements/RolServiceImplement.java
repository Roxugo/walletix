package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Rol;
import pe.edu.upc.walletix.repositories.IRolRepository;
import pe.edu.upc.walletix.servicesinterfaces.IRolService;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImplement implements IRolService {
    @Autowired
    private IRolRepository rolRepository;

    @Override
    public List<Rol> list() {
        return rolRepository.findByEstadoRol(1);
    }

    @Override
    public Rol insert(Rol rol) {
        rol.setEstadoRol(1);
        return rolRepository.save(rol);
    }

    @Override
    public Optional<Rol> listId(int id) {
        return rolRepository.findByIdRolAndEstadoRol(id, 1);
    }

    @Override
    public void update(Rol rol) {
        rolRepository.save(rol);
    }

    @Override
    public void delete(int id) {
        // Borrado lógico: no se borra la fila, solo se marca como eliminada
        Optional<Rol> rol = rolRepository.findByIdRolAndEstadoRol(id, 1);
        if (rol.isPresent()) {
            rol.get().setEstadoRol(0);
            rolRepository.save(rol.get());
        }
    }

    @Override
    public Optional<Rol> buscarPorUsuarioYRol(int idUsuario, String rol) {
        return rolRepository.findByUsuarioIdUsuarioAndRol(idUsuario, rol);
    }

    @Override
    public long contarRolesActivosDeUsuario(int idUsuario) {
        return rolRepository.countByUsuarioIdUsuarioAndEstadoRol(idUsuario, 1);
    }

    @Override
    public long contarUsuariosActivosConRol(String rol) {
        return rolRepository.countByRolAndEstadoRolAndUsuarioEstadoUsuario(rol, 1, 1);
    }
}
