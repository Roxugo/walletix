package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Categorías de gastos e ingresos (Comida, Transporte...)
@Entity
@Table(name = "Categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCategoria;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombreCategoria", length = 50, nullable = false)
    private String nombreCategoria;

    // gasto o ingreso
    @Column(name = "tipoCategoria", length = 20, nullable = false)
    private String tipoCategoria;

    @Column(name = "urlIconoCategoria", length = 100, nullable = false)
    private String urlIconoCategoria;

    // Ej: #FF5733
    @Column(name = "colorHexCategoria", length = 7, nullable = false)
    private String colorHexCategoria;

    // true = categoría del sistema
    @Column(name = "predeterminadoCategoria", nullable = false)
    private boolean predeterminadoCategoria;

    // Borrado lógico: 1 = activo, 0 = eliminado
    @Column(name = "estadoCategoria", nullable = false)
    private Integer estadoCategoria = 1;

    public Categoria() {
    }

    public Categoria(int idCategoria, Usuario usuario, String nombreCategoria, String tipoCategoria, String urlIconoCategoria, String colorHexCategoria, boolean predeterminadoCategoria, Integer estadoCategoria) {
        this.idCategoria = idCategoria;
        this.usuario = usuario;
        this.nombreCategoria = nombreCategoria;
        this.tipoCategoria = tipoCategoria;
        this.urlIconoCategoria = urlIconoCategoria;
        this.colorHexCategoria = colorHexCategoria;
        this.predeterminadoCategoria = predeterminadoCategoria;
        this.estadoCategoria = estadoCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    public String getUrlIconoCategoria() {
        return urlIconoCategoria;
    }

    public void setUrlIconoCategoria(String urlIconoCategoria) {
        this.urlIconoCategoria = urlIconoCategoria;
    }

    public String getColorHexCategoria() {
        return colorHexCategoria;
    }

    public void setColorHexCategoria(String colorHexCategoria) {
        this.colorHexCategoria = colorHexCategoria;
    }

    public boolean isPredeterminadoCategoria() {
        return predeterminadoCategoria;
    }

    public void setPredeterminadoCategoria(boolean predeterminadoCategoria) {
        this.predeterminadoCategoria = predeterminadoCategoria;
    }

    public Integer getEstadoCategoria() {
        return estadoCategoria;
    }

    public void setEstadoCategoria(Integer estadoCategoria) {
        this.estadoCategoria = estadoCategoria;
    }
}
