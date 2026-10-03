package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Auditoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.repositories.IAuditoriaRepository;
import pe.edu.upc.walletix.repositories.IUsuarioRepository;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImplement implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IAuditoriaRepository auditoriaRepository;

    @Override
    public List<Usuario> list() {
        return usuarioRepository.findByEstadoUsuarioTrue();
    }

    @Override
    public Usuario insert(Usuario usuario) {
        // 1. Guardamos el nuevo usuario para que la BD le asigne su ID autoincremental
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        // 2. Registro automático en Auditoría (Fase 1: Creación)
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuarioRegistro(nuevoUsuario);
        auditoria.setFechaRegistro(LocalDateTime.now());
        auditoria.setEstado(true);

        // Los campos de edición y eliminación quedan en null por defecto
        auditoriaRepository.save(auditoria);

        return nuevoUsuario;
    }

    @Override
    public Optional<Usuario> listId(int id) {
        return usuarioRepository.findByIdUsuarioAndEstadoUsuarioTrue(id);
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);

        // Registro automático en Auditoría (Fase 2: Edición / Actualización)
        List<Auditoria> auditorias = auditoriaRepository.findByUsuarioRegistroIdUsuario(usuario.getIdUsuario());
        if (!auditorias.isEmpty()) {
            Auditoria auditoria = auditorias.get(auditorias.size() - 1);
            auditoria.setUsuarioEditar(usuario);
            auditoria.setFechaEditar(LocalDateTime.now());
            auditoriaRepository.save(auditoria);
        }
    }

    @Override
    public void delete(int id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario usuario = opt.get();
            usuario.setEstadoUsuario(false); // Inactivar usuario
            usuarioRepository.save(usuario); // Guardar cambio

            // Registro automático en Auditoría (Fase 3: Eliminación / Baja lógica)
            List<Auditoria> auditorias = auditoriaRepository.findByUsuarioRegistroIdUsuario(id);
            if (!auditorias.isEmpty()) {
                Auditoria auditoria = auditorias.get(auditorias.size() - 1);
                auditoria.setUsuarioEliminar(usuario);
                auditoria.setFechaEliminar(LocalDateTime.now());
                auditoria.setEstado(false); // Inactivar auditoría
                auditoriaRepository.save(auditoria);
            }
        }
    }
}
