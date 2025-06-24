package mscluna.com.app.mvc.view;

import java.awt.*;
import java.awt.print.*;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class TicketPrinter implements Printable {
    private JTable tablaVentas;
    private double totalVenta;

    public TicketPrinter(JTable tablaVentas, double totalVenta) {
        this.tablaVentas = tablaVentas;
        this.totalVenta = totalVenta;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2d = (Graphics2D) g;

        // Ignora los márgenes del PageFormat, imprime desde la esquina superior izquierda
        g2d.translate(0, 0);

        // Ajusta el ancho (200-210 para 58mm)
        int width = 200;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));

        int y = 10;
        // Encabezado centrado (ajustado al ancho real)
        String titulo = "TICKET DE VENTA";
        int strWidth = g2d.getFontMetrics().stringWidth(titulo);
        g2d.drawString(titulo, (width - strWidth) / 2, y); y += 12;
        g2d.drawString("Fecha: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()), 0, y); y += 12;
        g2d.drawString("--------------------------------", 0, y); y += 10;
        g2d.drawString("Prod      Cant P.Unit  Importe", 0, y); y += 10;
        g2d.drawString("--------------------------------", 0, y); y += 10;

        TableModel model = tablaVentas.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String prod = corta(model.getValueAt(i, 0).toString(), 8);
            String cant = model.getValueAt(i, 1).toString();
            String precio = model.getValueAt(i, 2).toString();
            String total = model.getValueAt(i, 3).toString();

            String linea = String.format("%-8s %4s %7s %7s", prod, cant, precio, total);
            g2d.drawString(linea, 0, y); y += 10;
        }

        g2d.drawString("--------------------------------", 0, y); y += 12;
        g2d.drawString(String.format("TOTAL: $%.2f", totalVenta), 0, y); y += 15;
        g2d.drawString("GRACIAS POR SU COMPRA!", 0, y); y += 12;
        g2d.drawString(" ", 0, y); // línea vacía para corte

        return PAGE_EXISTS;
    }

    // Limita el nombre del producto a n caracteres
    private String corta(String s, int n) {
        return (s.length() > n) ? s.substring(0, n) : s;
    }
}