package pe.edu.upc.walletix.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Clase 1: crea y valida los tokens JWT
@Component
public class JwtTokenUtil {

    private static final long DURACION_TOKEN = 5 * 60 * 60 * 1000; // 5 horas

    @Value("${jwt.secret:iV9SZtXfUGQfFqSk+r9whz6nTbMBiaFSio0kxaBSycUlrEkkUmlYzsHXFbX3dp/9uZqpF2nFDnwmONI3qbn+8Q==}")
    private String secreto;

    // Clave para firmar el token, a partir del secreto en Base64 (jwt.secret o el valor por defecto)
    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(getAllClaims(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(autoridad -> autoridad.getAuthority())
                .collect(Collectors.joining(",")));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + DURACION_TOKEN);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(obtenerClave())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return getUsernameFromToken(token).equals(userDetails.getUsername()) && !isExpired(token);
    }
}
