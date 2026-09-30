package pe.edu.upc.walletix.dtos;

public class MerchantDTO {
    private int idMerchant;
    private String nameMerchant;
    private String logoUrlMerchant;
    private Integer defaultCategoryIdMerchant;

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
