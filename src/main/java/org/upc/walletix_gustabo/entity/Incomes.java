package org.upc.walletix_gustabo.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table (name = "tm_Incomes")
public class Incomes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_income;

    @Column (name = "amount", nullable = false)
    private float cantidad;

    @Column (name = "date", nullable = false)
    private LocalDate fecha;

    @Column (name = "income_type", length = 100, nullable = false)
    private String tipo_ingreso;

    @Enumerated(EnumType.STRING)
    @Column (name = "frequency", nullable = false)
    private IncomeFrequency frequency;

    @Column (name = "source", length = 100, nullable = false)
    private String source;

    @Column (name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn (name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn (name = "id_category")
    private CategoryEntity category;

    public int getId_income() {
        return id_income;
    }

    public void setId_income(int id_income) {
        this.id_income = id_income;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo_ingreso() {
        return tipo_ingreso;
    }

    public void setTipo_ingreso(String tipo_ingreso) {
        this.tipo_ingreso = tipo_ingreso;
    }

    public IncomeFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(IncomeFrequency frequency) {
        this.frequency = frequency;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }
}
