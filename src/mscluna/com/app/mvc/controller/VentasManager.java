package mscluna.com.app.mvc.controller;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

public class VentasManager {
    private JTable tablaVentas;
    private JLabel mostrarTotalLabel;
    private JLabel cambioCliente;

    public VentasManager(JTable tablaVentas, JLabel mostrarTotalLabel, JLabel cambioCliente) {
        this.tablaVentas = tablaVentas;
        this.mostrarTotalLabel = mostrarTotalLabel;
        this.cambioCliente = cambioCliente;
    }

    // Obtiene los datos de la tabla en formato JSON con fecha/hora
    public String getVentasComoJson() {
        JSONArray ventasArray = new JSONArray();
        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            JSONObject venta = new JSONObject();
            venta.put("codigo", tablaVentas.getValueAt(i, 0));
            venta.put("descripcion", tablaVentas.getValueAt(i, 1));
            venta.put("cantidad", tablaVentas.getValueAt(i, 2));
            venta.put("importe", tablaVentas.getValueAt(i, 3));
            venta.put("precioUnitario", tablaVentas.getValueAt(i, 4));
            venta.put("precioTotal", tablaVentas.getValueAt(i, 5));
            venta.put("tipo", tablaVentas.getValueAt(i, 6)); // importante para auditoría
            ventasArray.put(venta);
        }
        JSONObject root = new JSONObject();
        LocalDateTime now = LocalDateTime.now();
        root.put("fecha", now.toLocalDate().toString());
        root.put("hora", now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        root.put("ventas", ventasArray);
        return root.toString(2); // pretty print
    }

    public double calcularTotal() {
        double total = 0.0;
        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            Object precioTotalObj = tablaVentas.getValueAt(i, 5);
            double precioTotal = 0.0;
            if (precioTotalObj != null) {
                if (precioTotalObj instanceof Number) {
                    precioTotal = ((Number) precioTotalObj).doubleValue();
                } else {
                    try {
                        precioTotal = Double.parseDouble(precioTotalObj.toString());
                    } catch (NumberFormatException e) { precioTotal = 0.0; }
                }
            }
            total += precioTotal;
        }
        mostrarTotalLabel.setText(String.format("%.2f", total));
        return total;
    }

    public void calcularCambio(double pagaCon) {
        double total = calcularTotal();
        double cambio = pagaCon - total;
        cambioCliente.setText(String.format("%.2f", cambio));
    }

    // ¡Aquí está la corrección importante!
    public void descontarProductosEnBD(OperacionesBD operacionesBD) {
        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            String codigo = tablaVentas.getValueAt(i, 0).toString();
            float cantidad = Float.parseFloat(tablaVentas.getValueAt(i, 2).toString());
            String tipo = "";
            if (tablaVentas.getColumnCount() > 6 && tablaVentas.getValueAt(i, 6) != null) {
                tipo = tablaVentas.getValueAt(i, 6).toString().trim().toLowerCase();
            }
            if ("granel".equals(tipo)) {
                operacionesBD.descontarInventarioProductoGranel(codigo, cantidad);
            } else {
                operacionesBD.descontarInventarioProducto(codigo, cantidad);
            }
        }
    }

    public void registrarVentaEnBD(OperacionesBD operacionesBD, double total) {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.toLocalDate().toString();
        String hora = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        operacionesBD.insertarVenta(fecha, hora, total);
    }

    public void limpiarTablaVentas() {
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        model.setRowCount(0);
        calcularTotal();
        cambioCliente.setText("");
    }
}