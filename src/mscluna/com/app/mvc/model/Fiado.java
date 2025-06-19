/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mscluna.com.app.mvc.model;

/**
 *
 * @author luiis
 */
import java.time.LocalDateTime;

public class Fiado {
    private int id;
    private int clienteId;
    private int productoId;
    private int cantidad;
    private double total;
    private LocalDateTime fechaFiado;
    private boolean pagado;

    // Constructor vacío
    public Fiado() {}

    // Constructor completo
    public Fiado(int id, int clienteId, int productoId, int cantidad, double total, LocalDateTime fechaFiado, boolean pagado) {
        this.id = id;
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.total = total;
        this.fechaFiado = fechaFiado;
        this.pagado = pagado;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public LocalDateTime getFechaFiado() { return fechaFiado; }
    public void setFechaFiado(LocalDateTime fechaFiado) { this.fechaFiado = fechaFiado; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
}