package pe.edu.upc.walletix.securities;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

// Datos del usuario que inició sesión (sale del token JWT).
// Lo usan los controllers para que un USUARIO solo trabaje con sus propios datos; un ADMIN puede con todos
@Component
public class UsuarioActual {
    @Autowired
    private IUsuarioService usuarioService;

    public boolean esAdmin(Authentication autenticacion) {
        return autenticacion.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ADMIN"));
    }

    // Id del usuario que inició sesión (0 si ya no existe)
    public int id( Authentication autenticacion) {
        return usuarioService.buscarPorCorreo(autenticacion.getName())
                .map(Usuario::getIdUsuario)
                .orElse(0);
    }

    // true si puede trabajar con los datos de ese usuario: es el mismo usuario o es un ADMIN
    public boolean puedeGestionar(Authentication autenticacion, int idUsuario) {
        return esAdmin(autenticacion) || id(autenticacion) == idUsuario;
    }

    // Una categoría se puede usar si es predeterminada (de todos) o si pertenece al dueño del registro
    public boolean categoriaDisponible(Categoria categoria, int idUsuario) {
        return categoria.isPredeterminadoCategoria()
                || (categoria.getUsuario() != null && categoria.getUsuario().getIdUsuario() == idUsuario);
    }
}
