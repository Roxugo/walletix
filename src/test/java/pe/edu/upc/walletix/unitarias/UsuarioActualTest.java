package pe.edu.upc.walletix.unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import pe.edu.upc.walletix.entities.Categoria;
import pe.edu.upc.walletix.entities.Usuario;
import pe.edu.upc.walletix.securities.UsuarioActual;
import pe.edu.upc.walletix.servicesinterfaces.IUsuarioService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Pruebas UNITARIAS de UsuarioActual: la regla "cada usuario solo trabaja con sus propios datos"
// (US11, US13, US14). El servicio de usuarios se reemplaza por un simulacro (Mockito), no hay base de datos.
class UsuarioActualTest {

    private UsuarioActual usuarioActual;
    private IUsuarioService usuarioService;

    private Authentication sesion(String correo, String rol) {
        return new UsernamePasswordAuthenticationToken(correo, null, List.of(new SimpleGrantedAuthority(rol)));
    }

    private Usuario usuario(int id) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        return usuario;
    }

    @BeforeEach
    void preparar() {
        usuarioService = mock(IUsuarioService.class);
        usuarioActual = new UsuarioActual();
        ReflectionTestUtils.setField(usuarioActual, "usuarioService", usuarioService);
        when(usuarioService.buscarPorCorreo("ana@walletix.test")).thenReturn(Optional.of(usuario(7)));
        when(usuarioService.buscarPorCorreo("borrado@walletix.test")).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("id() devuelve el id del usuario del token")
    void idDelUsuario() {
        assertThat(usuarioActual.id(sesion("ana@walletix.test", "USUARIO"))).isEqualTo(7);
    }

    @Test
    @DisplayName("id() devuelve 0 si el usuario del token ya no existe")
    void idDeUsuarioInexistente() {
        assertThat(usuarioActual.id(sesion("borrado@walletix.test", "USUARIO"))).isZero();
    }

    @Test
    @DisplayName("Un USUARIO puede gestionar sus datos, pero no los de otro")
    void usuarioSoloSusDatos() {
        Authentication ana = sesion("ana@walletix.test", "USUARIO");
        assertThat(usuarioActual.puedeGestionar(ana, 7)).isTrue();
        assertThat(usuarioActual.puedeGestionar(ana, 8)).isFalse();
    }

    @Test
    @DisplayName("Un ADMIN puede gestionar los datos de cualquier usuario")
    void adminTodo() {
        Authentication admin = sesion("admin@walletix.test", "ADMIN");
        assertThat(usuarioActual.esAdmin(admin)).isTrue();
        assertThat(usuarioActual.puedeGestionar(admin, 8)).isTrue();
    }

    @Test
    @DisplayName("Una categoría se puede usar si es predeterminada o si es del dueño del registro")
    void categoriaDisponible() {
        Categoria predeterminada = new Categoria();
        predeterminada.setPredeterminadoCategoria(true);

        Categoria deAna = new Categoria();
        deAna.setUsuario(usuario(7));

        assertThat(usuarioActual.categoriaDisponible(predeterminada, 99)).isTrue();
        assertThat(usuarioActual.categoriaDisponible(deAna, 7)).isTrue();
        assertThat(usuarioActual.categoriaDisponible(deAna, 8)).isFalse();
    }
}
