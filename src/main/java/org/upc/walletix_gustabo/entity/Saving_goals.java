package org.upc.walletix_gustabo.entity;

import jakarta.persistence.*;
import org.modelmapper.internal.bytebuddy.asm.Advice;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tm_saving_goals")
public class Saving_goals {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id_saving_goals;

    @Column (name = "title", length = 150, nullable = false)
    private String titulo;

    @Column (name = "target_amount", nullable = false)
    private BigDecimal target_amount;

    @Column (name = "current_amount", nullable = false)
    private BigDecimal current_amount = BigDecimal.ZERO;

    @Column (name = "deadline", nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private GoalStatus status;

    @ManyToOne
    @JoinColumn (name = "id_user")
    private User user;

    public int getId_saving_goals() {
        return id_saving_goals;
    }

    public void setId_saving_goals(int id_saving_goals) {
        this.id_saving_goals = id_saving_goals;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getTarget_amount() {
        return target_amount;
    }

    public void setTarget_amount(BigDecimal target_amount) {
        this.target_amount = target_amount;
    }

    public BigDecimal getCurrent_amount() {
        return current_amount;
    }

    public void setCurrent_amount(BigDecimal current_amount) {
        this.current_amount = current_amount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
