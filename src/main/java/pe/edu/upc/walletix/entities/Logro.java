package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Achievement")
public class Logro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAchievement;

    @Column(name = "nameAchievement",length =20 ,nullable =false )
    private String nameAchievement;

    @Column(name = "descriptionAchievement", length = 30, nullable = false)
    private String descriptionAchievement;

    @Column(name = "iconurlAchievement", length = 60, nullable = false)
    private String iconurlAchievement;

    @Column(name = "pointAchievement", nullable = false)
    private int pointAchievement;

    public Logro() {
    }

    public Logro(int idAchievement, String nameAchievement, String descriptionAchievement, String iconurlAchievement, int pointAchievement) {
        this.idAchievement = idAchievement;
        this.nameAchievement = nameAchievement;
        this.descriptionAchievement = descriptionAchievement;
        this.iconurlAchievement = iconurlAchievement;
        this.pointAchievement = pointAchievement;
    }

    public int getIdAchievement() {
        return idAchievement;
    }

    public void setIdAchievement(int idAchievement) {
        this.idAchievement = idAchievement;
    }

    public String getNameAchievement() {
        return nameAchievement;
    }

    public void setNameAchievement(String nameAchievement) {
        this.nameAchievement = nameAchievement;
    }

    public String getDescriptionAchievement() {
        return descriptionAchievement;
    }

    public void setDescriptionAchievement(String descriptionAchievement) {
        this.descriptionAchievement = descriptionAchievement;
    }

    public String getIconurlAchievement() {
        return iconurlAchievement;
    }

    public void setIconurlAchievement(String iconurlAchievement) {
        this.iconurlAchievement = iconurlAchievement;
    }

    public int getPointAchievement() {
        return pointAchievement;
    }

    public void setPointAchievement(int pointAchievement) {
        this.pointAchievement = pointAchievement;
    }
}
