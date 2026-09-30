package pe.edu.upc.walletix.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Merchants")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMerchant;

    @Column(name = "nameMerchant", length = 100, nullable = false)
    private String nameMerchant;

    @Column(name = "logoUrlMerchant", length = 255)
    private String logoUrlMerchant;

    @Column(name = "defaultCategoryIdMerchant")
    private Integer defaultCategoryIdMerchant;

    public Merchant() {
    }

    public Merchant(int idMerchant, String nameMerchant, String logoUrlMerchant, Integer defaultCategoryIdMerchant) {
        this.idMerchant = idMerchant;
        this.nameMerchant = nameMerchant;
        this.logoUrlMerchant = logoUrlMerchant;
        this.defaultCategoryIdMerchant = defaultCategoryIdMerchant;
    }

    public int getIdMerchant() {
        return idMerchant;
    }

    public void setIdMerchant(int idMerchant) {
        this.idMerchant = idMerchant;
    }

    public String getNameMerchant() {
        return nameMerchant;
    }

    public void setNameMerchant(String nameMerchant) {
        this.nameMerchant = nameMerchant;
    }

    public String getLogoUrlMerchant() {
        return logoUrlMerchant;
    }

    public void setLogoUrlMerchant(String logoUrlMerchant) {
        this.logoUrlMerchant = logoUrlMerchant;
    }

    public Integer getDefaultCategoryIdMerchant() {
        return defaultCategoryIdMerchant;
    }

    public void setDefaultCategoryIdMerchant(Integer defaultCategoryIdMerchant) {
        this.defaultCategoryIdMerchant = defaultCategoryIdMerchant;
    }
}
