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

    public Producto(String id, String description, float unitPrice, int amount, String category, String discount, float importe, int minimum) {
        this.id = id;
        this.description = description;
        this.unitPrice = unitPrice;
        this.amount = amount;
        this.category = category;
        this.discount = discount;
        this.importe = importe;
        this.minimum = minimum;
    }

    public Producto(String description, float unitPrice, int amount, String category, String discount, float importe, int minimum) {
        this(null, description, unitPrice, amount, category, discount, importe, minimum);
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public float getUnitPrice() { return unitPrice; }
    public int getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDiscount() { return discount; }
    public float getImporte() { return importe; }
    public int getMinimum() { return minimum; }

    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setUnitPrice(float unitPrice) { this.unitPrice = unitPrice; }
    public void setAmount(int amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDiscount(String discount) { this.discount = discount; }
    public void setImporte(float importe) { this.importe = importe; }
    public void setMinimum(int minimum) { this.minimum = minimum; }
}