package pe.edu.upc.walletix.dtos;

public class CategoryDTO {
    private int idCategory;
    private Integer userIdCategory;
    private String nameCategory;
    private String typeCategory;
    private String iconUrlCategory;
    private String colorHexCategory;
    private boolean isDefaultCategory;

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public Integer getUserIdCategory() {
        return userIdCategory;
    }

    public void setUserIdCategory(Integer userIdCategory) {
        this.userIdCategory = userIdCategory;
    }

    public String getNameCategory() {
        return nameCategory;
    }

    public void setNameCategory(String nameCategory) {
        this.nameCategory = nameCategory;
    }

    public String getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(String typeCategory) {
        this.typeCategory = typeCategory;
    }

    public String getIconUrlCategory() {
        return iconUrlCategory;
    }

    public void setIconUrlCategory(String iconUrlCategory) {
        this.iconUrlCategory = iconUrlCategory;
    }

    public String getColorHexCategory() {
        return colorHexCategory;
    }

    public void setColorHexCategory(String colorHexCategory) {
        this.colorHexCategory = colorHexCategory;
    }

    public boolean isDefaultCategory() {
        return isDefaultCategory;
    }

    public void setDefaultCategory(boolean defaultCategory) {
        isDefaultCategory = defaultCategory;
    }
}
