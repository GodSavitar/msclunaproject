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

public class Usuario {
    private int id;
    private String username;
    private String passwordHash;
    private String nombre;
    private boolean usarProductosComunes;
    private boolean aplicarDescuentos;
    private boolean revisarHistorialVentas;
    private boolean cobrarTicket;
    private boolean eliminarArticulosVenta;
    private boolean usarBuscadorProductos;
    private boolean usarVarios;
    private boolean avalarProductos;
    private boolean manejarVariosTickets;
    private boolean admClientesAvalados;
    private boolean anadirProductos;
    private boolean eliminarProductos;
    private boolean editarProductos;
    private boolean verInventario;
    private boolean administrador;
    private LocalDateTime fechaCreacion;

    // Constructor vacío
    public Usuario() {}

    // Constructor con todos los campos (puedes generar más constructores según tus necesidades)
    public Usuario(int id, String username, String passwordHash, String nombre,
                   boolean usarProductosComunes, boolean aplicarDescuentos, boolean revisarHistorialVentas,
                   boolean cobrarTicket, boolean eliminarArticulosVenta, boolean usarBuscadorProductos,
                   boolean usarVarios, boolean avalarProductos, boolean manejarVariosTickets,
                   boolean admClientesAvalados, boolean anadirProductos, boolean eliminarProductos,
                   boolean editarProductos, boolean verInventario, boolean administrador,
                   LocalDateTime fechaCreacion) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.usarProductosComunes = usarProductosComunes;
        this.aplicarDescuentos = aplicarDescuentos;
        this.revisarHistorialVentas = revisarHistorialVentas;
        this.cobrarTicket = cobrarTicket;
        this.eliminarArticulosVenta = eliminarArticulosVenta;
        this.usarBuscadorProductos = usarBuscadorProductos;
        this.usarVarios = usarVarios;
        this.avalarProductos = avalarProductos;
        this.manejarVariosTickets = manejarVariosTickets;
        this.admClientesAvalados = admClientesAvalados;
        this.anadirProductos = anadirProductos;
        this.eliminarProductos = eliminarProductos;
        this.editarProductos = editarProductos;
        this.verInventario = verInventario;
        this.administrador = administrador;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y setters
    // ... (puedes generarlos automáticamente en tu IDE)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public boolean isUsarProductosComunes() { return usarProductosComunes; }
    public void setUsarProductosComunes(boolean usarProductosComunes) { this.usarProductosComunes = usarProductosComunes; }

    public boolean isAplicarDescuentos() { return aplicarDescuentos; }
    public void setAplicarDescuentos(boolean aplicarDescuentos) { this.aplicarDescuentos = aplicarDescuentos; }

    public boolean isRevisarHistorialVentas() { return revisarHistorialVentas; }
    public void setRevisarHistorialVentas(boolean revisarHistorialVentas) { this.revisarHistorialVentas = revisarHistorialVentas; }

    public boolean isCobrarTicket() { return cobrarTicket; }
    public void setCobrarTicket(boolean cobrarTicket) { this.cobrarTicket = cobrarTicket; }

    public boolean isEliminarArticulosVenta() { return eliminarArticulosVenta; }
    public void setEliminarArticulosVenta(boolean eliminarArticulosVenta) { this.eliminarArticulosVenta = eliminarArticulosVenta; }

    public boolean isUsarBuscadorProductos() { return usarBuscadorProductos; }
    public void setUsarBuscadorProductos(boolean usarBuscadorProductos) { this.usarBuscadorProductos = usarBuscadorProductos; }

    public boolean isUsarVarios() { return usarVarios; }
    public void setUsarVarios(boolean usarVarios) { this.usarVarios = usarVarios; }

    public boolean isAvalarProductos() { return avalarProductos; }
    public void setAvalarProductos(boolean avalarProductos) { this.avalarProductos = avalarProductos; }

    public boolean isManejarVariosTickets() { return manejarVariosTickets; }
    public void setManejarVariosTickets(boolean manejarVariosTickets) { this.manejarVariosTickets = manejarVariosTickets; }

    public boolean isAdmClientesAvalados() { return admClientesAvalados; }
    public void setAdmClientesAvalados(boolean admClientesAvalados) { this.admClientesAvalados = admClientesAvalados; }

    public boolean isAnadirProductos() { return anadirProductos; }
    public void setAnadirProductos(boolean anadirProductos) { this.anadirProductos = anadirProductos; }

    public boolean isEliminarProductos() { return eliminarProductos; }
    public void setEliminarProductos(boolean eliminarProductos) { this.eliminarProductos = eliminarProductos; }

    public boolean isEditarProductos() { return editarProductos; }
    public void setEditarProductos(boolean editarProductos) { this.editarProductos = editarProductos; }

    public boolean isVerInventario() { return verInventario; }
    public void setVerInventario(boolean verInventario) { this.verInventario = verInventario; }

    public boolean isAdministrador() { return administrador; }
    public void setAdministrador(boolean administrador) { this.administrador = administrador; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}