package pe.edu.upc.walletix.dtos;

public class UsuarioLogrosCountDTO {
    private String nombreUsuario;
    private Long totalLogros;

    public UsuarioLogrosCountDTO() {}

    public UsuarioLogrosCountDTO(String nombreUsuario, Long totalLogros) {
        this.nombreUsuario = nombreUsuario;
        this.totalLogros = totalLogros;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public Long getTotalLogros() { return totalLogros; }
    public void setTotalLogros(Long totalLogros) { this.totalLogros = totalLogros; }
}
