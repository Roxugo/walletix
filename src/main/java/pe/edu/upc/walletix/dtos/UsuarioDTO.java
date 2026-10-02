package pe.edu.upc.walletix.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UsuarioDTO {
    private int idUser;
    private String nameUser;
    private String emailUser;
    private String phoneUser;
    private LocalDate birthdayUser;
    private String segmentUser;
    private BigDecimal currentbalanceUser;
    private int gamificationpointsUser;

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
}
