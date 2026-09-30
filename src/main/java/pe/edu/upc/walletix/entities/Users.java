package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUser;

    @Column(name = "nameUser", length = 30, nullable = false)
    private String nameUser;

    @Column(name = "emailUser", length = 20, nullable = false)
    private String emailUser;

    @Column(name = "phoneUser", length = 9, nullable = false)
    private String phoneUser;

    @Column(name = "birthdayUser", length = 20, nullable = false)
    private LocalDate birthdayUser;

    @Column(name = "segmentUser", length = 20, nullable = false)
    private String segmentUser;

    @Column(name = "currentbalanceUser", nullable = false)
    private BigDecimal currentbalanceUser;

    @Column(name = "gamificationpointsUser", nullable = false)
    private int gamificationpointsUser;

    @OneToMany(mappedBy = "user")
    private List<UserChallenges> userChallenges = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Notifications> notifications = new ArrayList<>();

    public Users() {
    }

    public Users(int idUser, String nameUser, String emailUser, String phoneUser, LocalDate birthdayUser, String segmentUser, BigDecimal currentbalanceUser, int gamificationpointsUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.emailUser = emailUser;
        this.phoneUser = phoneUser;
        this.birthdayUser = birthdayUser;
        this.segmentUser = segmentUser;
        this.currentbalanceUser = currentbalanceUser;
        this.gamificationpointsUser = gamificationpointsUser;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public LocalDate getBirthdayUser() {
        return birthdayUser;
    }

    public void setBirthdayUser(LocalDate birthdayUser) {
        this.birthdayUser = birthdayUser;
    }

    public String getSegmentUser() {
        return segmentUser;
    }

    public void setSegmentUser(String segmentUser) {
        this.segmentUser = segmentUser;
    }

    public BigDecimal getCurrentbalanceUser() {
        return currentbalanceUser;
    }

    public void setCurrentbalanceUser(BigDecimal currentbalanceUser) {
        this.currentbalanceUser = currentbalanceUser;
    }

    public int getGamificationpointsUser() {
        return gamificationpointsUser;
    }

    public void setGamificationpointsUser(int gamificationpointsUser) {
        this.gamificationpointsUser = gamificationpointsUser;
    }

    public List<UserChallenges> getUserChallenges() {
        return userChallenges;
    }

    public void setUserChallenges(List<UserChallenges> userChallenges) {
        this.userChallenges = userChallenges;
    }

    public List<Notifications> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notifications> notifications) {
        this.notifications = notifications;
    }
}
