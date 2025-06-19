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

public class ClienteAvalado {
    private int id;
    private String nombre;
    private String telefono;
    private String direccion;
    private double totalFiado;
    private LocalDateTime fechaRegistro;

    // Constructor vacío
    public ClienteAvalado() {}

    // Constructor completo
    public ClienteAvalado(int id, String nombre, String telefono, String direccion, double totalFiado, LocalDateTime fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.totalFiado = totalFiado;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public double getTotalFiado() { return totalFiado; }
    public void setTotalFiado(double totalFiado) { this.totalFiado = totalFiado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}