package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCategory;

    @Column(name = "userIdCategory")
    private Integer userIdCategory;

    @Column(name = "nameCategory", length = 50, nullable = false)
    private String nameCategory;

    @Column(name = "typeCategory", length = 10, nullable = false)
    private String typeCategory;

    @Column(name = "iconUrlCategory", length = 255)
    private String iconUrlCategory;

    @Column(name = "colorHexCategory", length = 7)
    private String colorHexCategory;

    @Column(name = "isDefaultCategory", nullable = false)
    private boolean isDefaultCategory;

    public Category() {
    }

    public Category(int idCategory, Integer userIdCategory, String nameCategory, String typeCategory, String iconUrlCategory, String colorHexCategory, boolean isDefaultCategory) {
        this.idCategory = idCategory;
        this.userIdCategory = userIdCategory;
        this.nameCategory = nameCategory;
        this.typeCategory = typeCategory;
        this.iconUrlCategory = iconUrlCategory;
        this.colorHexCategory = colorHexCategory;
        this.isDefaultCategory = isDefaultCategory;
    }

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
