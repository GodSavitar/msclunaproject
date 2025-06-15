package mscluna.com.app.mvc.model;

public class ProductoGranel {
    private String idGProducts;
    private String descriptionGranel;
    private float prizeForKg;
    private float amountForKg;
    private float minimumAmount;
    private float prizeForUnit;
    private String categoryg;

    public ProductoGranel(String idGProducts, String descriptionGranel, float prizeForKg, float amountForKg, float minimumAmount, float prizeForUnit, String categoryg) {
        this.idGProducts = idGProducts;
        this.descriptionGranel = descriptionGranel;
        this.prizeForKg = prizeForKg;
        this.amountForKg = amountForKg;
        this.minimumAmount = minimumAmount;
        this.prizeForUnit = prizeForUnit;
        this.categoryg = categoryg;
    }

    // Getters y setters
    public String getIdGProducts() { return idGProducts; }
    public void setIdGProducts(String idGProducts) { this.idGProducts = idGProducts; }
    public String getDescriptionGranel() { return descriptionGranel; }
    public void setDescriptionGranel(String descriptionGranel) { this.descriptionGranel = descriptionGranel; }
    public float getPrizeForKg() { return prizeForKg; }
    public void setPrizeForKg(float prizeForKg) { this.prizeForKg = prizeForKg; }
    public float getAmountForKg() { return amountForKg; }
    public void setAmountForKg(float amountForKg) { this.amountForKg = amountForKg; }
    public float getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(float minimumAmount) { this.minimumAmount = minimumAmount; }
    public float getPrizeForUnit() { return prizeForUnit; }
    public void setPrizeForUnit(float prizeForUnit) { this.prizeForUnit = prizeForUnit; }
    public String getCategoryg() { return categoryg; }
    public void setCategoryg(String categoryg) { this.categoryg = categoryg; }
}