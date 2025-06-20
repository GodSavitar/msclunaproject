/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mscluna.com.app.mvc.view;

import mscluna.com.app.mvc.controller.ConexionBD;
import mscluna.com.app.mvc.controller.Sesion;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author luiis
 */
public class historialCortes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(historialCortes.class.getName());

    /**
     * Creates new form historialCortes
     */
    public historialCortes() {
        initComponents();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cargarTablaCortesCaja();
    }
    private void cargarTablaCortesCaja() {
        String[] columnas = {"ID", "Cajero", "Turno", "Fecha Corte", "Hora Corte", "Corte", "Dinero", "Monto Corte"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        try (
            Connection conn = ConexionBD.getConexion(Sesion.getUsuario(), Sesion.getContrasena(), Sesion.getBaseDatos());
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM cash_cuts");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                Object[] fila = new Object[] {
                    rs.getInt("id_cut"),
                    rs.getString("cashier"),
                    rs.getString("day_shift"),
                    rs.getString("date_cut"),
                    rs.getString("hour_cut"),
                    rs.getDouble("cut"),
                    rs.getDouble("money"),
                    rs.getDouble("amount_cut")
                };
                modelo.addRow(fila);
            }
            tablaCortesCaja.setModel(modelo);
        } catch (SQLException e) {
            logger.log(java.util.logging.Level.SEVERE, "Error al cargar cortes de caja", e);
            javax.swing.JOptionPane.showMessageDialog(this, "Error cargando cortes de caja: " + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCortesCaja = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(0, 204, 204));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Historial Cortes de Caja");
        jLabel1.setOpaque(true);
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 780, 50));

        tablaCortesCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaCortesCaja);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 69, 730, 400));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new historialCortes().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaCortesCaja;
    // End of variables declaration//GEN-END:variables
}
