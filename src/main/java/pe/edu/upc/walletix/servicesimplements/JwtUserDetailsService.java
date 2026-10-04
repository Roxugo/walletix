package pe.edu.upc.walletix.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.repositories.IUsuarioRepository;

import java.util.ArrayList;
import java.util.List;

// Clase 2: busca al usuario (por correo) y sus roles para Spring Security
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreoUsuarioAndEstadoUsuario(correo, 1)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        List<GrantedAuthority> roles = new ArrayList<>();
        usuario.getRoles().forEach(rol -> roles.add(new SimpleGrantedAuthority(rol.getRol())));

        return new User(usuario.getCorreoUsuario(), usuario.getContrasenaUsuario(), true, true, true, true, roles);
    }
}
