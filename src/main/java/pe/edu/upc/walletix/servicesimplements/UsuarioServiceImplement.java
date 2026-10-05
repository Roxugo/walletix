package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Usuario que inició sesión y está haciendo la acción (sale del token JWT).
    // Si no hay sesión, se usa el usuario afectado
    private Usuario usuarioQueRealizaLaAccion(Usuario usuarioAfectado) {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !autenticacion.isAuthenticated() || autenticacion instanceof AnonymousAuthenticationToken) {
            return usuarioAfectado;
        }
        return usuarioRepository.findByCorreoUsuarioIgnoreCaseAndEstadoUsuario(autenticacion.getName(), 1).orElse(usuarioAfectado);
    }

    @Override
    public List<Usuario> list() {
        return usuarioRepository.findByEstadoUsuario(1);
    }

    @Override
    public Usuario insert(Usuario usuario) {
        // 1. Guardamos el nuevo usuario para que la BD le asigne su ID autoincremental
        Usuario nuevoUsuario = usuarioRepository.save(usuario);

        // 2. Registro automático en Auditoría (Fase 1: Creación)
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuarioRegistro(nuevoUsuario);
        auditoria.setFechaRegistro(LocalDateTime.now());
        auditoria.setEstado(1);

        // Los campos de edición y eliminación quedan en null por defecto
        auditoriaRepository.save(auditoria);

        return nuevoUsuario;
    }

    @Override
    public Optional<Usuario> listId(int id) {
        return usuarioRepository.findByIdUsuarioAndEstadoUsuario(id, 1);
    }

    @Override
    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);

        // Registro automático en Auditoría (Fase 2: Edición / Actualización)
        List<Auditoria> auditorias = auditoriaRepository.findByUsuarioRegistroIdUsuario(usuario.getIdUsuario());
        if (!auditorias.isEmpty()) {
            Auditoria auditoria = auditorias.get(auditorias.size() - 1);
            auditoria.setUsuarioEditar(usuarioQueRealizaLaAccion(usuario)); // quién hizo la edición
            auditoria.setFechaEditar(LocalDateTime.now());
            auditoriaRepository.save(auditoria);
        }
    }

    @Override
    public void delete(int id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario usuario = opt.get();
            // Se busca antes de inactivar, por si el usuario se elimina a sí mismo
            Usuario eliminadoPor = usuarioQueRealizaLaAccion(usuario);
            usuario.setEstadoUsuario(0); // Inactivar usuario (Soft delete con 0)
            usuarioRepository.save(usuario); // Guardar cambio

            // Registro automático en Auditoría (Fase 3: Eliminación / Baja lógica)
            List<Auditoria> auditorias = auditoriaRepository.findByUsuarioRegistroIdUsuario(id);
            if (!auditorias.isEmpty()) {
                Auditoria auditoria = auditorias.get(auditorias.size() - 1);
                auditoria.setUsuarioEliminar(eliminadoPor); // quién hizo la eliminación
                auditoria.setFechaEliminar(LocalDateTime.now());
                auditoria.setEstado(0); // Inactivar auditoría (0)
                auditoriaRepository.save(auditoria);
            }
        }
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreoUsuarioIgnoreCase(correo);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoUsuarioIgnoreCaseAndEstadoUsuario(correo, 1);
    }
}
