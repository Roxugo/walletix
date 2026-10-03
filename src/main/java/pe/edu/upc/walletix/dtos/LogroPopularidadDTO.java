package pe.edu.upc.walletix.dtos;

public class LogroPopularidadDTO {
    private String nombreLogro;
    private Long cantidadUsuarios;

    public LogroPopularidadDTO() {}

    public LogroPopularidadDTO(String nombreLogro, Long cantidadUsuarios) {
        this.nombreLogro = nombreLogro;
        this.cantidadUsuarios = cantidadUsuarios;
    }

    public String getNombreLogro() { return nombreLogro; }
    public void setNombreLogro(String nombreLogro) { this.nombreLogro = nombreLogro; }

    public Long getCantidadUsuarios() { return cantidadUsuarios; }
    public void setCantidadUsuarios(Long cantidadUsuarios) { this.cantidadUsuarios = cantidadUsuarios; }
}
