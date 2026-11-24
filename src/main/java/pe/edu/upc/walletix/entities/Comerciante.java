package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

// Comercios donde el usuario realiza sus gastos (US21)
@Entity
@Table(name = "Comerciante")
public class Comerciante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idComerciante;

    // Categoría habitual del comercio
    @ManyToOne
    @JoinColumn(name = "idCategoria", nullable = false)
    private Categoria categoria;

    // Dueño del comercio. Vacío (null) = predeterminado: lo creó un ADMIN y lo ven todos.
    // Con dueño = comercio propio de un cliente, solo lo ve él.
    // Es una columna nueva que acepta null: Hibernate la agrega sola y los comercios que ya existían quedan como predeterminados.
    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @Column(name = "nombreComerciante", length = 50, nullable = false)
    private String nombreComerciante;

    @Column(name = "urlLogoComerciante", length = 100, nullable = false)
    private String urlLogoComerciante;

    // Borrado lógico: 1 = activo, 0 = eliminado
    @Column(name = "estadoComerciante", nullable = false)
    private Integer estadoComerciante = 1;

    public Comerciante() {
    }

    public Comerciante(int idComerciante, Categoria categoria, String nombreComerciante, String urlLogoComerciante, Integer estadoComerciante) {
        this.idComerciante = idComerciante;
        this.categoria = categoria;
        this.nombreComerciante = nombreComerciante;
        this.urlLogoComerciante = urlLogoComerciante;
        this.estadoComerciante = estadoComerciante;
    }

    public int getIdComerciante() {
        return idComerciante;
    }

    public void setIdComerciante(int idComerciante) {
        this.idComerciante = idComerciante;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // true si es de todos (no tiene dueño)
    public boolean esPredeterminado() {
        return usuario == null;
    }

    public String getNombreComerciante() {
        return nombreComerciante;
    }

    public void setNombreComerciante(String nombreComerciante) {
        this.nombreComerciante = nombreComerciante;
    }

    public String getUrlLogoComerciante() {
        return urlLogoComerciante;
    }

    public void setUrlLogoComerciante(String urlLogoComerciante) {
        this.urlLogoComerciante = urlLogoComerciante;
    }

    public Integer getEstadoComerciante() {
        return estadoComerciante;
    }

    public void setEstadoComerciante(Integer estadoComerciante) {
        this.estadoComerciante = estadoComerciante;
    }
}
