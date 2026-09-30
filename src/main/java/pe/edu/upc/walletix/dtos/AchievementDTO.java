package pe.edu.upc.walletix.dtos;



public class AchievementDTO {
    private int idAchievement;
    private String nameAchievement;
    private String descriptionAchievement;
    private String iconurlAchievement;
    private int pointAchievement;

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
