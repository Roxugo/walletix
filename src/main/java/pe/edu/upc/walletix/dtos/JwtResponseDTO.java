package pe.edu.upc.walletix.dtos;

// Respuesta del login: el token que se debe enviar en las siguientes peticiones
public class JwtResponseDTO {
    private final String token;

    public JwtResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
