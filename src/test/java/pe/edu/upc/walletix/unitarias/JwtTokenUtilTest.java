package pe.edu.upc.walletix.unitarias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import pe.edu.upc.walletix.securities.JwtTokenUtil;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Pruebas UNITARIAS de JwtTokenUtil (US02: inicio de sesión): sin Spring, sin base de datos.
class JwtTokenUtilTest {

    private static final String SECRETO =
            "iV9SZtXfUGQfFqSk+r9whz6nTbMBiaFSio0kxaBSycUlrEkkUmlYzsHXFbX3dp/9uZqpF2nFDnwmONI3qbn+8Q==";

    private JwtTokenUtil jwtTokenUtil;
    private UserDetails ana;

    @BeforeEach
    void preparar() {
        jwtTokenUtil = new JwtTokenUtil();
        // El secreto normalmente lo pone Spring con @Value; aquí se coloca a mano
        ReflectionTestUtils.setField(jwtTokenUtil, "secreto", SECRETO);
        ana = new User("ana@walletix.test", "x", List.of(new SimpleGrantedAuthority("USUARIO")));
    }

    @Test
    @DisplayName("El token guarda el correo del usuario como subject")
    void tokenGuardaElCorreo() {
        String token = jwtTokenUtil.generateToken(ana);
        assertThat(jwtTokenUtil.getUsernameFromToken(token)).isEqualTo("ana@walletix.test");
    }

    @Test
    @DisplayName("El token guarda los roles del usuario")
    void tokenGuardaLosRoles() {
        String token = jwtTokenUtil.generateToken(ana);
        String roles = jwtTokenUtil.getClaim(token, claims -> claims.get("roles", String.class));
        assertThat(roles).isEqualTo("USUARIO");
    }

    @Test
    @DisplayName("El token vence en 5 horas")
    void tokenVenceEnCincoHoras() {
        long antes = System.currentTimeMillis();
        Date vencimiento = jwtTokenUtil.getExpirationDate(jwtTokenUtil.generateToken(ana));
        long cincoHoras = 5L * 60 * 60 * 1000;
        // getExpirationDate pierde los milisegundos (el JWT guarda segundos), por eso hay un margen de 1 segundo
        assertThat(vencimiento.getTime()).isBetween(antes + cincoHoras - 1000, antes + cincoHoras + 1000);
    }

    @Test
    @DisplayName("validateToken acepta el token de su dueño y rechaza el de otro usuario")
    void validaSoloAlDueno() {
        String token = jwtTokenUtil.generateToken(ana);
        UserDetails luis = new User("luis@walletix.test", "x", List.of());
        assertThat(jwtTokenUtil.validateToken(token, ana)).isTrue();
        assertThat(jwtTokenUtil.validateToken(token, luis)).isFalse();
    }

    @Test
    @DisplayName("Un token modificado ya no se puede leer (la firma no coincide)")
    void tokenModificadoFalla() {
        String token = jwtTokenUtil.generateToken(ana);
        String alterado = token.substring(0, token.length() - 4) + "AAAA";
        assertThatThrownBy(() -> jwtTokenUtil.getUsernameFromToken(alterado))
                .isInstanceOf(io.jsonwebtoken.JwtException.class);
    }
}
