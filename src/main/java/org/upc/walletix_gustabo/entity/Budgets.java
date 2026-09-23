package org.upc.walletix_gustabo.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "tm_Budgets")
public class Budgets {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_budget;

    @Column (name = "month", nullable = false)
    private int mes;

    @Column (name = "year", nullable = false)
    private int anio;

    @Column (name = "allocated_amount", nullable = false)
    private float importe_asignado;

    @ManyToOne
    @JoinColumn (name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn (name = "id_category")
    private CategoryEntity category;

    public int getId_budget() {
        return id_budget;
    }

    public void setId_budget(int id_budget) {
        this.id_budget = id_budget;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public float getImporte_asignado() {
        return importe_asignado;
    }

    public void setImporte_asignado(float importe_asignado) {
        this.importe_asignado = importe_asignado;
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
