package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotification;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private Users user;

    @Column(name = "typeNotification", length = 30, nullable = false)
    private String typeNotification;

    @Column(name = "titleNotification", length = 150, nullable = false)
    private String titleNotification;

    @Column(name = "messageNotification", columnDefinition = "TEXT", nullable = false)
    private String messageNotification;

    @Column(name = "readNotification")
    private boolean readNotification;

    public Notifications() {
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(String typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getTitleNotification() {
        return titleNotification;
    }

    public void setTitleNotification(String titleNotification) {
        this.titleNotification = titleNotification;
    }

    public String getMessageNotification() {
        return messageNotification;
    }

    public void setMessageNotification(String messageNotification) {
        this.messageNotification = messageNotification;
    }

    public boolean isReadNotification() {
        return readNotification;
    }

    public void setReadNotification(boolean readNotification) {
        this.readNotification = readNotification;
    }
}