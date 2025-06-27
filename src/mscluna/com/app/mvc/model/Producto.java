package mscluna.com.app.mvc.model;

public class Producto {
    private String id;
    private String description;
    private float unitPrice;
    private int amount;
    private String category;
    private String discount;
    private float importe;
    private int minimum;
    private float prizeCost;
    private float prizeWholesale;

    public Producto(String id, String description, float unitPrice, int amount, String category, String discount, float importe, int minimum, float prizeCost, float prizeWholesale) {
        this.id = id;
        this.description = description;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.category = category;
        this.discount = discount;
        this.importe = importe;
        this.minimum = minimum;
        this.prizeCost = prizeCost;
        this.prizeWholesale = prizeWholesale;
    }

    public Producto(String description, float unitPrice, int amount, String category, String discount, float importe, int minimum, float prizeCost, float prizeWholesale) {
        this(null, description, unitPrice, amount, category, discount, importe, minimum, prizeCost, prizeWholesale);
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public float getUnitPrice() { return unitPrice; }
    public int getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDiscount() { return discount; }
    public float getImporte() { return importe; }
    public int getMinimum() { return minimum; }
    public float getPrizeCost() { return prizeCost; }
    public float getPrizeWholesale() { return prizeWholesale; }

    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDiscount(String discount) { this.discount = discount; }
    public void setImporte(float importe) { this.importe = importe; }
    public void setMinimum(int minimum) { this.minimum = minimum; }
    public void setPrizeCost(float prizeCost) { this.prizeCost = prizeCost; }
    public void setPrizeWholesale(float prizeWholesale) { this.prizeWholesale = prizeWholesale; }
}