/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mscluna.com.app.mvc.view;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import mscluna.com.app.mvc.controller.BotonesManager;
import mscluna.com.app.mvc.controller.OperacionesBD;
import mscluna.com.app.mvc.controller.Sesion;
import mscluna.com.app.mvc.controller.VentasManager;
import mscluna.com.app.mvc.model.Producto;
import mscluna.com.app.mvc.view.preLobby;
import javax.swing.JCheckBox;
import javax.swing.DefaultCellEditor;


public class lobby extends javax.swing.JFrame {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(lobby.class.getName());
    private OperacionesBD operacionesBD;
    private VentasManager ventasManager;
    private double totalVentasTurno = 0.0;
    private String cajero;
    private String turno;
    private double dineroInicialTurno = 0.0;
    
public lobby(String cajero, String turno, String dineroInicial) {

    this.cajero = cajero;
    this.turno = turno;
    try {
        this.dineroInicialTurno = Double.parseDouble(dineroInicial);
    } catch (NumberFormatException e) {
        this.dineroInicialTurno = 0.0;
    }    
    initComponents();
    descuentosCheckBox.addActionListener(e -> recalculaTotal());
    this.setLocationRelativeTo(null);
    nombreCajeroBg.setText(cajero);
    cajeroEnTurno.setText(cajero);
    if (turno.equalsIgnoreCase("Mañana")) {
        horaTurno.setText("De 7:00 a.m a las 3:00 p.m");
    } else {
        horaTurno.setText("De las 3:00 p.m a las 10:00 p.m");
    }
    actualizaLabelsTotales();

    ventasManager = new VentasManager(tablaVentas, mostrarTotalLabel, cambioCliente);
    operacionesBD = new OperacionesBD(
        Sesion.getUsuario(),
        Sesion.getContrasena(),
        Sesion.getBaseDatos()
    );
    cargarHistorialATabla();
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i = 0; i < tablaVentas.getColumnCount(); i++) {
        tablaVentas.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }
    DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
// Agrega la columna "Aplica Importe" si no existe
        if (model.findColumn("Aplica Importe") == -1) {
            model.addColumn("Aplica Importe");
        }

        // Agrega la columna "Eliminar" si no existe
        if (model.findColumn("Eliminar") == -1) {
            model.addColumn("Eliminar");
        }

        // Asigna el botón eliminar y el checkbox al modelo de la tabla
        tablaVentas.getColumn("Eliminar").setCellRenderer(new ButtonRenderer());
        tablaVentas.getColumn("Eliminar").setCellEditor(new ButtonEditor(new JCheckBox()));
        tablaVentas.getColumn("Aplica Importe").setCellEditor(new DefaultCellEditor(new JCheckBox()));
    while (model.getRowCount() > 0) {
        model.removeRow(0);
    }
    cargarInventarioATabla();
}
private void actualizaLabelsTotales() {
    entradasDeDinero.setText(String.format("%.2f", totalVentasTurno));
    double dineroEsperadoEnCaja = dineroInicialTurno + totalVentasTurno;
    dineroEsperado.setText(String.format("%.2f", dineroEsperadoEnCaja));
}
private void cargarHistorialATabla() {
    List<Object[]> ventas = operacionesBD.obtenerVentasHistorial();
    String[] columnas = {"ID", "Fecha", "Hora", "Total"};
    DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
    for (Object[] fila : ventas) {
        modelo.addRow(fila);
    }
    historialTable.setModel(modelo);
}

private static class InventarioCellRenderer extends DefaultTableCellRenderer {
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // Las columnas: 3 = "Cantidad", 7 = "Mínimo"
        try {
            int cantidad = Integer.parseInt(table.getValueAt(row, 3).toString());
            int minimo = Integer.parseInt(table.getValueAt(row, 7).toString());
            if (cantidad <= minimo) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(Color.BLACK);
            }
        } catch (Exception e) {
            c.setForeground(Color.BLACK); // fallback
        }
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        } else {
            c.setBackground(Color.WHITE);
        }
        return c;
        }
}
 
public void agregarProductoGranelAVentaDesdeBuscar(mscluna.com.app.mvc.model.ProductoGranel producto, float kg, float precio) {
    if (producto == null || kg <= 0) return;
    DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
    boolean encontrado = false;
    for (int i = 0; i < model.getRowCount(); i++) {
        Object idObj = model.getValueAt(i, 0);
        if (idObj != null && idObj.toString().equals(producto.getIdGProducts())) {
            float cantidadActual = Float.parseFloat(model.getValueAt(i, 2).toString());
            float nuevaCantidad = cantidadActual + kg;
            float precioUnitario = producto.getPrizeForKg();
            float importe = 0f;
            float precioTotal = precioUnitario * nuevaCantidad + importe;
            model.setValueAt(nuevaCantidad, i, 2); // Actualiza cantidad (kg)
            model.setValueAt(precioTotal, i, 5);   // Actualiza precio total
            encontrado = true;
            break;
        }
    }
    if (!encontrado) {
        float precioUnitario = producto.getPrizeForKg();
        float importe = 0f;
        float precioTotal = precio; // ya viene calculado desde buscarProductos
        model.addRow(new Object[]{
        producto.getIdGProducts(),
        producto.getDescriptionGranel(),
        kg,
        importe,
        precioUnitario,
        precioTotal,
        "granel"
        });
    }
    ventasManager.calcularTotal();
}
public void agregarProductoAVentaDesdeBuscar(Producto producto, int cantidad) {
    if (producto == null || cantidad <= 0) return;
    DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
    boolean encontrado = false;
    for (int i = 0; i < model.getRowCount(); i++) {
        Object idObj = model.getValueAt(i, 0);
        if (idObj != null && idObj.toString().equals(producto.getId())) {
            int cantidadActual = Integer.parseInt(model.getValueAt(i, 2).toString());
            int nuevaCantidad = cantidadActual + cantidad;
            float precioUnitario = producto.getUnitPrice();
            float importe = producto.getImporte();
            float precioTotal = precioUnitario * nuevaCantidad + importe;
            model.setValueAt(nuevaCantidad, i, 2); // Actualiza cantidad
            model.setValueAt(precioTotal, i, 5);   // Actualiza precio total
            encontrado = true;
            break;
        }
    }
    if (!encontrado) {
        float precioUnitario = producto.getUnitPrice();
        float importe = producto.getImporte();
        float precioTotal = precioUnitario * cantidad + importe;
        model.addRow(new Object[]{
            producto.getId(),
            producto.getDescription(),
            cantidad,
            importe,
            precioUnitario,
            precioTotal,
            "normal" // <- Tipo
        });
    }
    ventasManager.calcularTotal();
}

private void cargarInventarioATabla() {
    // ---- Para productos normales (tablaInventarioGFV) ----
    List<Producto> productos = operacionesBD.obtenerTodosProductos();
    String[] columnasGFV = {"ID", "Descripción", "Precio Unitario", "Cantidad", "Categoría", "Descuento", "Importe", "Mínimo"};
    DefaultTableModel modeloGFV = new DefaultTableModel(columnasGFV, 0);
    for (Producto p : productos) {
        modeloGFV.addRow(new Object[]{
            p.getId(),
            p.getDescription(),
            p.getUnitPrice(),
            p.getAmount(),
            p.getCategory(),
            p.getDiscount(),
            p.getImporte(),
            p.getMinimum()
        });
    }
    tablaInventarioGFV.setModel(modeloGFV);

    InventarioCellRenderer inventarioCellRenderer = new InventarioCellRenderer();
    for (int i = 0; i < tablaInventarioGFV.getColumnCount(); i++) {
        tablaInventarioGFV.getColumnModel().getColumn(i).setCellRenderer(inventarioCellRenderer);
    }

    // ---- Para productos granel/frutas/verduras (tablaInventario) ----
    List<mscluna.com.app.mvc.model.ProductoGranel> productosGranel = operacionesBD.obtenerTodosProductosGranel();
    String[] columnasGranel = {"ID", "Descripción", "Precio x Kg", "Cantidad x Kg", "Mínimo Kg", "Precio x Unidad", "Categoría"};
    DefaultTableModel modeloGranel = new DefaultTableModel(columnasGranel, 0);
    for (mscluna.com.app.mvc.model.ProductoGranel g : productosGranel) {
        modeloGranel.addRow(new Object[]{
            g.getIdGProducts(),
            g.getDescriptionGranel(),
            g.getPrizeForKg(),
            g.getAmountForKg(),
            g.getMinimumAmount(),
            g.getPrizeForUnit(),
            g.getCategoryg()
        });
    }
    
    tablaInventario.setModel(modeloGranel);
    // Puedes poner un renderer especial aquí si quieres.
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ventasPanel = new javax.swing.JPanel();
        agregarProductoButton = new javax.swing.JButton();
        codProducto = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaVentas = new javax.swing.JTable();
        cobrarButton = new javax.swing.JButton();
        pagaConLabel = new javax.swing.JLabel();
        totalLabel = new javax.swing.JLabel();
        mostrarTotalLabel = new javax.swing.JLabel();
        cambioPagar = new javax.swing.JFormattedTextField();
        cambioLabelText = new javax.swing.JLabel();
        cambioCliente = new javax.swing.JLabel();
        codLabelInst1 = new javax.swing.JLabel();
        buscarProductoVenta = new javax.swing.JButton();
        descuentosCheckBox = new javax.swing.JCheckBox();
        agregarVariosButton = new javax.swing.JButton();
        productosPanel = new javax.swing.JPanel();
        labelEntradaProducto = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        productosCodEntrada1 = new javax.swing.JPanel();
        editarProdCategoria = new javax.swing.JButton();
        eliminarProductoCategoria = new javax.swing.JButton();
        codProductoIngresar = new javax.swing.JLabel();
        frmCodigoIngresar = new javax.swing.JFormattedTextField();
        descripcionProductoIngresar = new javax.swing.JLabel();
        txtDescIngresar = new javax.swing.JTextField();
        precioUnitarioIngresar = new javax.swing.JLabel();
        frmPrecioUnitarioIngresar = new javax.swing.JFormattedTextField();
        existenciasIngresar = new javax.swing.JLabel();
        frmExistenciasIngresar = new javax.swing.JFormattedTextField();
        categoriaIngresar = new javax.swing.JLabel();
        txtCategoriaIngresar = new javax.swing.JTextField();
        descuentoIngresar = new javax.swing.JLabel();
        frmDescuentoIngresar = new javax.swing.JFormattedTextField();
        importeProductoIngresar = new javax.swing.JLabel();
        frmImporteIngresar = new javax.swing.JFormattedTextField();
        frmMinimoIngresar = new javax.swing.JFormattedTextField();
        minimoLabel = new javax.swing.JLabel();
        aceptarEntradaProducto = new javax.swing.JButton();
        productosGranelFrutVerdEntrada2 = new javax.swing.JPanel();
        codProductoIngresarGFV = new javax.swing.JLabel();
        frmCodigoIngresarGFV = new javax.swing.JFormattedTextField();
        descripcionProductoIngresarGFV = new javax.swing.JLabel();
        txtDescIngresarGFV = new javax.swing.JTextField();
        precioUnitarioIngresarGFV = new javax.swing.JLabel();
        frmPrecioUnitarioIngresarGFV = new javax.swing.JFormattedTextField();
        frmExistenciasIngresarGFV = new javax.swing.JFormattedTextField();
        existenciasIngresarGFV = new javax.swing.JLabel();
        categoriaIngresarGFV = new javax.swing.JLabel();
        txtCategoriaIngresarGFV = new javax.swing.JTextField();
        minimoPorKgGFV = new javax.swing.JLabel();
        frmMinimoIngresarGFV = new javax.swing.JFormattedTextField();
        aceptarEntradaProductoGFV = new javax.swing.JButton();
        editarProdCategoriaGFV = new javax.swing.JButton();
        eliminarProductoCategoriaGFV = new javax.swing.JButton();
        precioPorKgIngresarGFV = new javax.swing.JLabel();
        frmPrecioPorKgGFV = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        inventarioPanel = new javax.swing.JPanel();
        actualizarTablaInventario = new javax.swing.JButton();
        buscarProductoInventario = new javax.swing.JTextField();
        aceptarBusquedaInventario = new javax.swing.JButton();
        labelBuscarProdInv = new javax.swing.JLabel();
        labelAccesoBDInventario = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaInventarioGFV = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaInventario = new javax.swing.JTable();
        cortePanel = new javax.swing.JPanel();
        welcomeLabel = new javax.swing.JLabel();
        cajeroEnTurno = new javax.swing.JLabel();
        dineroEsperadoLabel = new javax.swing.JLabel();
        dineroEsperado = new javax.swing.JLabel();
        dineroEnCajaLabel = new javax.swing.JLabel();
        dineroEnCaja = new javax.swing.JFormattedTextField();
        horaTurno = new javax.swing.JLabel();
        entradaLabel = new javax.swing.JLabel();
        entradasDeDinero = new javax.swing.JLabel();
        cortarButton = new javax.swing.JButton();
        cerrarTurnoButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        historialCortesButton = new javax.swing.JButton();
        historialPanel = new javax.swing.JPanel();
        labelEntradaProducto1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        historialTable = new javax.swing.JTable();
        eliminarVentasAll = new javax.swing.JButton();
        bgLobby = new javax.swing.JPanel();
        mscLabel = new javax.swing.JLabel();
        lunaLabel = new javax.swing.JLabel();
        historialButton = new javax.swing.JButton();
        ventasButton = new javax.swing.JButton();
        productosButton = new javax.swing.JButton();
        inventarioButton = new javax.swing.JButton();
        corteButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        indicadorPagina = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        leAtiendeLabel = new javax.swing.JLabel();
        nombreCajeroBg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 730));
        setMinimumSize(new java.awt.Dimension(1280, 730));
        setPreferredSize(new java.awt.Dimension(1280, 720));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ventasPanel.setBackground(new java.awt.Color(255, 255, 255));
        ventasPanel.setMaximumSize(new java.awt.Dimension(1280, 560));
        ventasPanel.setMinimumSize(new java.awt.Dimension(1280, 560));
        ventasPanel.setPreferredSize(new java.awt.Dimension(1280, 560));
        ventasPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        agregarProductoButton.setBackground(new java.awt.Color(59, 153, 97));
        agregarProductoButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        agregarProductoButton.setForeground(new java.awt.Color(255, 255, 255));
        agregarProductoButton.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceEnterKey.png")));
        agregarProductoButton.setText("Agregar Producto");
        agregarProductoButton.setFocusPainted(false);
        agregarProductoButton.setFocusable(false);
        agregarProductoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductoButtonActionPerformed(evt);
            }
        });
        ventasPanel.add(agregarProductoButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, 240, 40));

        codProducto.setBackground(new java.awt.Color(255, 255, 255));
        codProducto.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(59, 153, 97)));
        ventasPanel.add(codProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 500, 40));
        codProducto.addActionListener(e -> agregarProductoButton.doClick());

        tablaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "Código", "Descripción", "Cantidad", "Importe", "Precio Unitario", "Descuento", "Precio Total", "Tipo", "Aplica Importe", "Eliminar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class,   // Código
                java.lang.String.class,   // Descripción
                java.lang.Double.class,   // Cantidad
                java.lang.Double.class,   // Importe
                java.lang.Double.class,   // Precio Unitario
                java.lang.Double.class,   // Descuento (precio sabatino)
                java.lang.Double.class,   // Precio Total
                java.lang.String.class,   // Tipo
                java.lang.Boolean.class,  // Aplica Importe
                java.lang.Object.class    // Eliminar
            };
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            public boolean isCellEditable(int row, int column) {
                // Solo permite editar para productos "varios"
                String tipo = (String) getValueAt(row, 7); // columna "Tipo"
                if ("varios".equals(tipo)) {
                    return column == 1 || column == 2 || column == 6; // Descripción, Cantidad, Precio Total
                }
                // Permite editar Aplica Importe y Eliminar
                return column == 8 || column == 9;
            }
        }
    );
    jScrollPane1.setViewportView(tablaVentas);

    ventasPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 1110, 390));

    cobrarButton.setBackground(new java.awt.Color(65, 220, 127));
    cobrarButton.setText("Cobrar");
    cobrarButton.setEnabled(false);
    cobrarButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cobrarButtonActionPerformed(evt);
        }
    });
    ventasPanel.add(cobrarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 460, 120, 50));

    pagaConLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    pagaConLabel.setForeground(new java.awt.Color(0, 153, 153));
    pagaConLabel.setText("PAGA CON:");
    ventasPanel.add(pagaConLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 460, -1, 40));

    totalLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    totalLabel.setForeground(new java.awt.Color(0, 153, 153));
    totalLabel.setText("TOTAL:");
    ventasPanel.add(totalLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 460, -1, 40));

    mostrarTotalLabel.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
    mostrarTotalLabel.setForeground(new java.awt.Color(51, 255, 0));
    ventasPanel.add(mostrarTotalLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 460, 130, 40));

    cambioPagar.setBackground(new java.awt.Color(255, 255, 255));
    ventasPanel.add(cambioPagar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 460, 110, 40));
    cambioPagar.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ventasManager.calcularCambio(Double.parseDouble(cambioPagar.getText()));
            cobrarButton.setEnabled(true); // Habilita el botón para cobrar
        }
    });

    cambioLabelText.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    cambioLabelText.setForeground(new java.awt.Color(0, 153, 153));
    cambioLabelText.setText("CAMBIO:");
    ventasPanel.add(cambioLabelText, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 460, -1, 40));

    cambioCliente.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
    cambioCliente.setForeground(new java.awt.Color(255, 0, 0));
    ventasPanel.add(cambioCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 460, 110, 40));

    codLabelInst1.setForeground(new java.awt.Color(0, 153, 153));
    codLabelInst1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    codLabelInst1.setText("Código del Producto:");
    ventasPanel.add(codLabelInst1, new org.netbeans.lib.awtextra.AbsoluteConstraints(33, 10, 130, 40));

    buscarProductoVenta.setBackground(new java.awt.Color(59, 153, 97));
    buscarProductoVenta.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    buscarProductoVenta.setForeground(new java.awt.Color(255, 255, 255));
    buscarProductoVenta.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceLupaWhite.png")));
    buscarProductoVenta.setText("Buscar Producto");
    buscarProductoVenta.setFocusPainted(false);
    buscarProductoVenta.setFocusable(false);
    buscarProductoVenta.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            buscarProductoVentaActionPerformed(evt);
        }
    });
    ventasPanel.add(buscarProductoVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 10, 160, 40));

    descuentosCheckBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
    descuentosCheckBox.setForeground(new java.awt.Color(102, 102, 102));
    descuentosCheckBox.setText("Descuentos");
    descuentosCheckBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            descuentosCheckBoxActionPerformed(evt);
        }
    });
    ventasPanel.add(descuentosCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 460, 110, 40));

    agregarVariosButton.setBackground(new java.awt.Color(59, 153, 97));
    agregarVariosButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    agregarVariosButton.setForeground(new java.awt.Color(255, 255, 255));
    agregarVariosButton.setText("Varios");
    agregarVariosButton.setFocusPainted(false);
    agregarVariosButton.setFocusable(false);
    agregarVariosButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            agregarVariosButtonActionPerformed(evt);
        }
    });
    ventasPanel.add(agregarVariosButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 10, 100, 40));

    getContentPane().add(ventasPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, -1, 590));

    productosPanel.setBackground(new java.awt.Color(255, 255, 255));
    productosPanel.setMaximumSize(new java.awt.Dimension(1280, 560));
    productosPanel.setMinimumSize(new java.awt.Dimension(1280, 560));
    productosPanel.setPreferredSize(new java.awt.Dimension(1280, 560));
    productosPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    productosPanel.setVisible(false);

    labelEntradaProducto.setBackground(new java.awt.Color(0, 205, 203));
    labelEntradaProducto.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
    labelEntradaProducto.setForeground(new java.awt.Color(255, 255, 255));
    labelEntradaProducto.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelEntradaProducto.setText("Entradas de Producto");
    labelEntradaProducto.setOpaque(true);
    productosPanel.add(labelEntradaProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 1220, 40));

    jTabbedPane1.setForeground(new java.awt.Color(0, 0, 0));

    productosCodEntrada1.setBackground(new java.awt.Color(255, 255, 255));
    productosCodEntrada1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    editarProdCategoria.setBackground(new java.awt.Color(65, 220, 127));
    editarProdCategoria.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    editarProdCategoria.setForeground(new java.awt.Color(255, 255, 255));
    editarProdCategoria.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceEditarIcon.png")));
    editarProdCategoria.setText("Editar");
    editarProdCategoria.setFocusPainted(false);
    editarProdCategoria.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            editarProdCategoriaActionPerformed(evt);
        }
    });
    productosCodEntrada1.add(editarProdCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, -1, -1));

    eliminarProductoCategoria.setBackground(new java.awt.Color(65, 220, 127));
    eliminarProductoCategoria.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    eliminarProductoCategoria.setForeground(new java.awt.Color(255, 255, 255));
    eliminarProductoCategoria.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceEliminarIcon.png")));
    eliminarProductoCategoria.setText("Eliminar");
    eliminarProductoCategoria.setFocusPainted(false);
    eliminarProductoCategoria.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            eliminarProductoCategoriaActionPerformed(evt);
        }
    });
    productosCodEntrada1.add(eliminarProductoCategoria, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 10, -1, -1));

    codProductoIngresar.setForeground(new java.awt.Color(0, 0, 0));
    codProductoIngresar.setText("Código de producto:");
    productosCodEntrada1.add(codProductoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 120, 40));

    frmCodigoIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmCodigoIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmCodigoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 380, 40));

    descripcionProductoIngresar.setForeground(new java.awt.Color(0, 0, 0));
    descripcionProductoIngresar.setText("Descripción del producto:");
    productosCodEntrada1.add(descripcionProductoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 150, 40));

    txtDescIngresar.setBackground(new java.awt.Color(255, 255, 255));
    txtDescIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(txtDescIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 60, 380, 40));

    precioUnitarioIngresar.setForeground(new java.awt.Color(0, 0, 0));
    precioUnitarioIngresar.setText("Precio unitario (normal):");
    productosCodEntrada1.add(precioUnitarioIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 150, 40));

    frmPrecioUnitarioIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmPrecioUnitarioIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmPrecioUnitarioIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 110, 380, 40));

    existenciasIngresar.setForeground(new java.awt.Color(0, 0, 0));
    existenciasIngresar.setText("Existencias (Cantidad):");
    productosCodEntrada1.add(existenciasIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 140, 40));

    frmExistenciasIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmExistenciasIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmExistenciasIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, 380, 40));

    categoriaIngresar.setForeground(new java.awt.Color(0, 0, 0));
    categoriaIngresar.setText("Categoria:");
    productosCodEntrada1.add(categoriaIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 210, 70, 40));

    txtCategoriaIngresar.setBackground(new java.awt.Color(255, 255, 255));
    txtCategoriaIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(txtCategoriaIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 210, 380, 40));

    descuentoIngresar.setForeground(new java.awt.Color(0, 0, 0));
    descuentoIngresar.setText("Precio unitario (sabados): ");
    productosCodEntrada1.add(descuentoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 110, 150, 40));

    frmDescuentoIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmDescuentoIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmDescuentoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 110, 380, 40));

    importeProductoIngresar.setForeground(new java.awt.Color(0, 0, 0));
    importeProductoIngresar.setText("Importe:");
    productosCodEntrada1.add(importeProductoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 260, 60, 40));

    frmImporteIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmImporteIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmImporteIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 260, 380, 40));

    frmMinimoIngresar.setBackground(new java.awt.Color(255, 255, 255));
    frmMinimoIngresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosCodEntrada1.add(frmMinimoIngresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 310, 380, 40));

    minimoLabel.setForeground(new java.awt.Color(0, 0, 0));
    minimoLabel.setText("Cantidad minima de producto:");
    productosCodEntrada1.add(minimoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 180, 40));

    aceptarEntradaProducto.setBackground(new java.awt.Color(65, 220, 127));
    aceptarEntradaProducto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    aceptarEntradaProducto.setForeground(new java.awt.Color(255, 255, 255));
    aceptarEntradaProducto.setText("Aceptar");
    aceptarEntradaProducto.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            aceptarEntradaProductoActionPerformed(evt);
        }
    });
    productosCodEntrada1.add(aceptarEntradaProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 340, 120, 40));

    jTabbedPane1.addTab("Productos Cod", productosCodEntrada1);

    productosGranelFrutVerdEntrada2.setBackground(new java.awt.Color(255, 255, 255));
    productosGranelFrutVerdEntrada2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    codProductoIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    codProductoIngresarGFV.setText("Código de producto:");
    productosGranelFrutVerdEntrada2.add(codProductoIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 120, 40));

    frmCodigoIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    frmCodigoIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(frmCodigoIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 380, 40));

    descripcionProductoIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    descripcionProductoIngresarGFV.setText("Descripción del producto:");
    productosGranelFrutVerdEntrada2.add(descripcionProductoIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 150, 40));

    txtDescIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    txtDescIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(txtDescIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 380, 40));

    precioUnitarioIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    precioUnitarioIngresarGFV.setText("Precio unitario:");
    productosGranelFrutVerdEntrada2.add(precioUnitarioIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 110, 40));

    frmPrecioUnitarioIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    frmPrecioUnitarioIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(frmPrecioUnitarioIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, 380, 40));

    frmExistenciasIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    frmExistenciasIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(frmExistenciasIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, 380, 40));

    existenciasIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    existenciasIngresarGFV.setText("Existencias (Cantidad p/kg):");
    productosGranelFrutVerdEntrada2.add(existenciasIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, 170, 40));

    categoriaIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    categoriaIngresarGFV.setText("Categoria:");
    productosGranelFrutVerdEntrada2.add(categoriaIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 260, 80, 40));

    txtCategoriaIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    txtCategoriaIngresarGFV.setForeground(new java.awt.Color(255, 255, 255));
    txtCategoriaIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(txtCategoriaIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 260, 380, 40));

    minimoPorKgGFV.setForeground(new java.awt.Color(0, 0, 0));
    minimoPorKgGFV.setText("Cantidad minima de producto p/Kg:");
    productosGranelFrutVerdEntrada2.add(minimoPorKgGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 310, 220, 40));

    frmMinimoIngresarGFV.setBackground(new java.awt.Color(255, 255, 255));
    frmMinimoIngresarGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(frmMinimoIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 310, 380, 40));

    aceptarEntradaProductoGFV.setBackground(new java.awt.Color(65, 220, 127));
    aceptarEntradaProductoGFV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    aceptarEntradaProductoGFV.setForeground(new java.awt.Color(255, 255, 255));
    aceptarEntradaProductoGFV.setText("Aceptar");
    aceptarEntradaProductoGFV.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            aceptarEntradaProductoGFVActionPerformed(evt);
        }
    });
    productosGranelFrutVerdEntrada2.add(aceptarEntradaProductoGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 340, 120, 40));

    editarProdCategoriaGFV.setBackground(new java.awt.Color(65, 220, 127));
    editarProdCategoriaGFV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    editarProdCategoriaGFV.setForeground(new java.awt.Color(255, 255, 255));
    editarProdCategoriaGFV.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceEditarIcon.png")));
    editarProdCategoriaGFV.setText("Editar");
    editarProdCategoriaGFV.setFocusPainted(false);
    editarProdCategoriaGFV.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            editarProdCategoriaGFVActionPerformed(evt);
        }
    });
    productosGranelFrutVerdEntrada2.add(editarProdCategoriaGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 10, -1, -1));

    eliminarProductoCategoriaGFV.setBackground(new java.awt.Color(65, 220, 127));
    eliminarProductoCategoriaGFV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    eliminarProductoCategoriaGFV.setForeground(new java.awt.Color(255, 255, 255));
    eliminarProductoCategoriaGFV.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceEliminarIcon.png")));
    eliminarProductoCategoriaGFV.setText("Eliminar");
    eliminarProductoCategoriaGFV.setFocusPainted(false);
    eliminarProductoCategoriaGFV.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            eliminarProductoCategoriaGFVActionPerformed(evt);
        }
    });
    productosGranelFrutVerdEntrada2.add(eliminarProductoCategoriaGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(1050, 10, -1, -1));

    precioPorKgIngresarGFV.setForeground(new java.awt.Color(0, 0, 0));
    precioPorKgIngresarGFV.setText("Precio p/Kg:");
    productosGranelFrutVerdEntrada2.add(precioPorKgIngresarGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 160, 100, 40));

    frmPrecioPorKgGFV.setBackground(new java.awt.Color(255, 255, 255));
    frmPrecioPorKgGFV.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    productosGranelFrutVerdEntrada2.add(frmPrecioPorKgGFV, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 160, 380, 40));

    jTabbedPane1.addTab("Productos Granel/Frutas/Verduras", productosGranelFrutVerdEntrada2);

    productosPanel.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 1140, 420));

    jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    productosPanel.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 1170, 450));

    getContentPane().add(productosPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, -1, 590));

    inventarioPanel.setBackground(new java.awt.Color(255, 255, 255));
    inventarioPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    actualizarTablaInventario.setBackground(new java.awt.Color(65, 220, 127));
    actualizarTablaInventario.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    actualizarTablaInventario.setForeground(new java.awt.Color(255, 255, 255));
    actualizarTablaInventario.setText("Actualizar");
    actualizarTablaInventario.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            actualizarTablaInventarioActionPerformed(evt);
        }
    });
    inventarioPanel.add(actualizarTablaInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 20, 100, 30));

    buscarProductoInventario.setBackground(new java.awt.Color(255, 255, 255));
    buscarProductoInventario.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
    inventarioPanel.add(buscarProductoInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, 320, 30));
    buscarProductoInventario.addActionListener(new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            aceptarBusquedaInventario.doClick();
        }
    });

    aceptarBusquedaInventario.setBackground(new java.awt.Color(65, 220, 127));
    aceptarBusquedaInventario.setForeground(new java.awt.Color(255, 255, 255));
    aceptarBusquedaInventario.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceBuscar.png")));
    aceptarBusquedaInventario.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            aceptarBusquedaInventarioActionPerformed(evt);
        }
    });
    inventarioPanel.add(aceptarBusquedaInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 20, 40, 30));

    labelBuscarProdInv.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    labelBuscarProdInv.setForeground(new java.awt.Color(255, 255, 255));
    labelBuscarProdInv.setText("Buscar Producto:");
    inventarioPanel.add(labelBuscarProdInv, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 100, 50));

    labelAccesoBDInventario.setBackground(new java.awt.Color(0, 205, 203));
    labelAccesoBDInventario.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
    labelAccesoBDInventario.setForeground(new java.awt.Color(255, 255, 255));
    labelAccesoBDInventario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelAccesoBDInventario.setText("Acceso a BD | Inventario");
    labelAccesoBDInventario.setOpaque(true);
    inventarioPanel.add(labelAccesoBDInventario, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 1210, 50));

    jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    tablaInventarioGFV.setModel(new javax.swing.table.DefaultTableModel(
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
    jScrollPane2.setViewportView(tablaInventarioGFV);

    jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1150, 370));

    jTabbedPane2.addTab("Productos Tienda", jPanel1);

    jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    tablaInventario.setModel(new javax.swing.table.DefaultTableModel(
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
    jScrollPane3.setViewportView(tablaInventario);

    jPanel2.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 1150, 370));

    jTabbedPane2.addTab("Productos Granel/Frutas/Verduras", jPanel2);

    inventarioPanel.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 1190, 440));

    getContentPane().add(inventarioPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 1280, 590));
    inventarioPanel.setVisible(false);

    cortePanel.setBackground(new java.awt.Color(255, 255, 255));
    cortePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    welcomeLabel.setFont(new java.awt.Font("SimSun", 0, 36)); // NOI18N
    welcomeLabel.setForeground(new java.awt.Color(0, 0, 0));
    welcomeLabel.setText("Bienvenid@");
    cortePanel.add(welcomeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 18, 190, -1));

    cajeroEnTurno.setFont(new java.awt.Font("SimSun", 0, 36)); // NOI18N
    cajeroEnTurno.setForeground(new java.awt.Color(0, 0, 0));
    cortePanel.add(cajeroEnTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 16, 190, 40));
    cajeroEnTurno.setText(cajero);

    dineroEsperadoLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    dineroEsperadoLabel.setForeground(new java.awt.Color(0, 0, 0));
    dineroEsperadoLabel.setText("Dinero esperado en caja:");
    cortePanel.add(dineroEsperadoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 250, -1, 30));

    dineroEsperado.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    dineroEsperado.setForeground(new java.awt.Color(0, 0, 0));
    cortePanel.add(dineroEsperado, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 250, 110, 30));

    dineroEnCajaLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    dineroEnCajaLabel.setForeground(new java.awt.Color(0, 0, 0));
    dineroEnCajaLabel.setText("Dinero en caja:");
    cortePanel.add(dineroEnCajaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 300, -1, 40));
    cortePanel.add(dineroEnCaja, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 300, 130, 40));

    horaTurno.setFont(new java.awt.Font("SimSun", 0, 24)); // NOI18N
    horaTurno.setForeground(new java.awt.Color(102, 102, 102));
    cortePanel.add(horaTurno, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 420, 30));
    if(turno.equals("Matutino")){
        horaTurno.setText("De 7:00 a.m a las 3:00 p.m");
    } else {
        horaTurno.setText("De las 3:00 p.m a las 10:00 p.m");
    }

    entradaLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    entradaLabel.setForeground(new java.awt.Color(0, 0, 0));
    entradaLabel.setText("Entradas de dinero:");
    cortePanel.add(entradaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, -1, -1));

    entradasDeDinero.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
    entradasDeDinero.setForeground(new java.awt.Color(255, 102, 0));
    cortePanel.add(entradasDeDinero, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 130, 200, 30));

    cortarButton.setText("Cortar");
    cortarButton.setFocusPainted(false);
    cortarButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cortarButtonActionPerformed(evt);
        }
    });
    cortePanel.add(cortarButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 360, 110, 40));

    cerrarTurnoButton.setText("Cerrar turno");
    cerrarTurnoButton.setFocusPainted(false);
    cerrarTurnoButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cerrarTurnoButtonActionPerformed(evt);
        }
    });
    cortePanel.add(cerrarTurnoButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1038, 120, 140, 40));

    jSeparator4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    cortePanel.add(jSeparator4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 1170, 360));

    historialCortesButton.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceHistorial.png")));
    historialCortesButton.setFocusPainted(false);
    historialCortesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            historialCortesButtonActionPerformed(evt);
        }
    });
    cortePanel.add(historialCortesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 40, 50, 50));

    getContentPane().add(cortePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 1280, 590));
    cortePanel.setVisible(false);

    historialPanel.setBackground(new java.awt.Color(255, 255, 255));
    historialPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    labelEntradaProducto1.setFont(new java.awt.Font("SimSun", 0, 36)); // NOI18N
    labelEntradaProducto1.setForeground(new java.awt.Color(0, 0, 0));
    labelEntradaProducto1.setText("Historial de compras");
    historialPanel.add(labelEntradaProducto1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 410, 40));

    historialTable.setModel(new javax.swing.table.DefaultTableModel(
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
    jScrollPane4.setViewportView(historialTable);

    historialPanel.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 1150, 400));

    eliminarVentasAll.setText("Eliminar todos los registros");
    eliminarVentasAll.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            eliminarVentasAllActionPerformed(evt);
        }
    });
    historialPanel.add(eliminarVentasAll, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 20, -1, -1));

    getContentPane().add(historialPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 1280, 590));
    historialPanel.setVisible(false);

    bgLobby.setBackground(new java.awt.Color(255, 255, 255));
    bgLobby.setMaximumSize(new java.awt.Dimension(1280, 720));
    bgLobby.setMinimumSize(new java.awt.Dimension(1280, 720));
    bgLobby.setPreferredSize(new java.awt.Dimension(1280, 720));
    bgLobby.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

    mscLabel.setFont(new java.awt.Font("SimSun", 0, 24)); // NOI18N
    mscLabel.setForeground(new java.awt.Color(0, 205, 203));
    mscLabel.setText("MISCELANEA");
    bgLobby.add(mscLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 130, 40));

    lunaLabel.setFont(new java.awt.Font("SimSun", 0, 52)); // NOI18N
    lunaLabel.setForeground(new java.awt.Color(0, 153, 153));
    lunaLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lunaLabel.setText("LUNA");
    bgLobby.add(lunaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 120, 40));

    historialButton.setBackground(new java.awt.Color(59, 153, 97));
    historialButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    historialButton.setForeground(new java.awt.Color(255, 255, 255));
    historialButton.setText("Historial");
    historialButton.setBorderPainted(false);
    historialButton.setFocusCycleRoot(true);
    historialButton.setFocusPainted(false);
    historialButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            historialButtonMouseClicked(evt);
        }
    });
    historialButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            historialButtonActionPerformed(evt);
        }
    });
    bgLobby.add(historialButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 110, 140, 40));

    ventasButton.setBackground(new java.awt.Color(65, 220, 127));
    ventasButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    ventasButton.setForeground(new java.awt.Color(0, 0, 0));
    ventasButton.setText("Ventas");
    ventasButton.setBorderPainted(false);
    ventasButton.setFocusCycleRoot(true);
    ventasButton.setFocusPainted(false);
    ventasButton.setMaximumSize(new java.awt.Dimension(80, 30));
    ventasButton.setMinimumSize(new java.awt.Dimension(80, 30));
    ventasButton.setPreferredSize(new java.awt.Dimension(80, 30));
    ventasButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            ventasButtonMouseClicked(evt);
        }
    });
    ventasButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            ventasButtonActionPerformed(evt);
        }
    });
    bgLobby.add(ventasButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 140, 40));

    productosButton.setBackground(new java.awt.Color(59, 153, 97));
    productosButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    productosButton.setForeground(new java.awt.Color(255, 255, 255));
    productosButton.setText("Productos");
    productosButton.setBorderPainted(false);
    productosButton.setFocusCycleRoot(true);
    productosButton.setFocusPainted(false);
    productosButton.setMaximumSize(new java.awt.Dimension(80, 30));
    productosButton.setMinimumSize(new java.awt.Dimension(80, 30));
    productosButton.setPreferredSize(new java.awt.Dimension(80, 30));
    productosButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            productosButtonMouseClicked(evt);
        }
    });
    productosButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            productosButtonActionPerformed(evt);
        }
    });
    bgLobby.add(productosButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 110, 140, 40));

    inventarioButton.setBackground(new java.awt.Color(59, 153, 97));
    inventarioButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    inventarioButton.setForeground(new java.awt.Color(255, 255, 255));
    inventarioButton.setText("Inventario");
    inventarioButton.setBorderPainted(false);
    inventarioButton.setFocusCycleRoot(true);
    inventarioButton.setFocusPainted(false);
    inventarioButton.setMaximumSize(new java.awt.Dimension(80, 30));
    inventarioButton.setMinimumSize(new java.awt.Dimension(80, 30));
    inventarioButton.setPreferredSize(new java.awt.Dimension(80, 30));
    inventarioButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            inventarioButtonMouseClicked(evt);
        }
    });
    inventarioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            inventarioButtonActionPerformed(evt);
        }
    });
    bgLobby.add(inventarioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 110, 140, 40));

    corteButton.setBackground(new java.awt.Color(59, 153, 97));
    corteButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    corteButton.setForeground(new java.awt.Color(255, 255, 255));
    corteButton.setText("Corte");
    corteButton.setBorderPainted(false);
    corteButton.setFocusCycleRoot(true);
    corteButton.setFocusPainted(false);
    corteButton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            corteButtonMouseClicked(evt);
        }
    });
    corteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            corteButtonActionPerformed(evt);
        }
    });
    bgLobby.add(corteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 110, 140, 40));

    settingsButton.setBackground(null);
    settingsButton.setIcon(new ImageIcon(getClass().getResource("/mscluna/com/app/mvc/images/resourceConfig.png")));
    settingsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    settingsButton.setBorderPainted(false);
    settingsButton.setContentAreaFilled(false);
    settingsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            settingsButtonActionPerformed(evt);
        }
    });
    bgLobby.add(settingsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 110, 40, 40));

    indicadorPagina.setFont(new java.awt.Font("SimSun", 0, 48)); // NOI18N
    indicadorPagina.setForeground(new java.awt.Color(0, 153, 153));
    indicadorPagina.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    indicadorPagina.setText("VENTAS");
    bgLobby.add(indicadorPagina, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 100, 420, 60));

    jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 153)));
    bgLobby.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1280, 60));

    leAtiendeLabel.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
    leAtiendeLabel.setForeground(new java.awt.Color(0, 153, 153));
    leAtiendeLabel.setText("LE ATIENDE:");
    bgLobby.add(leAtiendeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 30, -1, 40));

    nombreCajeroBg.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
    nombreCajeroBg.setForeground(new java.awt.Color(51, 255, 0));
    bgLobby.add(nombreCajeroBg, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 30, 190, 40));
    nombreCajeroBg.setText(cajero);

    getContentPane().add(bgLobby, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 750));

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ventasButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ventasButtonActionPerformed
        ventasPanel.setVisible(true);
        productosPanel.setVisible(false);
        inventarioPanel.setVisible(false);
        cortePanel.setVisible(false);
        historialPanel.setVisible(false);
    }//GEN-LAST:event_ventasButtonActionPerformed

    private void productosButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productosButtonMouseClicked
         indicadorPagina.setText("PRODUCTOS");
         productosButton.setBackground(new Color(65,220,127));
         productosButton.setForeground(Color.BLACK);
         BotonesManager manager = new BotonesManager(List.of(historialButton,ventasButton,productosButton,inventarioButton,corteButton));
         manager.eliminarYActualizar(productosButton);
        
    }//GEN-LAST:event_productosButtonMouseClicked

    private void inventarioButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventarioButtonMouseClicked
         indicadorPagina.setText("INVENTARIO");
         inventarioButton.setBackground(new Color(65,220,127));
         inventarioButton.setForeground(Color.BLACK);
         BotonesManager manager = new BotonesManager(List.of(historialButton,ventasButton,productosButton,inventarioButton,corteButton));
         manager.eliminarYActualizar(inventarioButton);
    }//GEN-LAST:event_inventarioButtonMouseClicked

    private void historialButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historialButtonMouseClicked
         indicadorPagina.setText("HISTORIAL");
         historialButton.setBackground(new Color(65,220,127));
         historialButton.setForeground(Color.BLACK);
         BotonesManager manager = new BotonesManager(List.of(historialButton,ventasButton,productosButton,inventarioButton,corteButton));
         manager.eliminarYActualizar(historialButton);
    }//GEN-LAST:event_historialButtonMouseClicked

    private void ventasButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ventasButtonMouseClicked
         indicadorPagina.setText("VENTAS");
         ventasButton.setBackground(new Color(65,220,127));
         ventasButton.setForeground(Color.BLACK);
         BotonesManager manager = new BotonesManager(List.of(historialButton,ventasButton,productosButton,inventarioButton,corteButton));
         manager.eliminarYActualizar(ventasButton);
    }//GEN-LAST:event_ventasButtonMouseClicked

    private void corteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_corteButtonMouseClicked
         indicadorPagina.setText("CORTE");
         corteButton.setBackground(new Color(65,220,127));
         corteButton.setForeground(Color.BLACK);
         BotonesManager manager = new BotonesManager(List.of(historialButton,ventasButton,productosButton,inventarioButton,corteButton));
         manager.eliminarYActualizar(corteButton);
    }//GEN-LAST:event_corteButtonMouseClicked

    private void agregarProductoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarProductoButtonActionPerformed
        try {
            String id = codProducto.getText().trim();
            Producto producto = operacionesBD.obtenerProductoPorId(id);

            if (producto != null) {
                DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
                boolean encontrado = false;

                for (int i = 0; i < model.getRowCount(); i++) {
                    Object idObj = model.getValueAt(i, 0);
                    if (idObj != null && idObj.toString().equals(id)) {
                        // Validar cantidad no nula
                        Object cantidadObj = model.getValueAt(i, 2);
                        int cantidadActual = (cantidadObj != null && cantidadObj instanceof Integer) 
                            ? (Integer) cantidadObj : 0;
                        int nuevaCantidad = cantidadActual + 1;
                        // Validar precio unitario no nulo
                        Object precioUnitarioObj = model.getValueAt(i, 4);
                        float precioUnitario = (precioUnitarioObj != null && precioUnitarioObj instanceof Float)
                            ? (Float) precioUnitarioObj : producto.getUnitPrice();
                        // Validar importe no nulo
                        Object importeObj = model.getValueAt(i, 3);
                        float importe = (importeObj != null && importeObj instanceof Float)
                            ? (Float) importeObj : producto.getImporte();

                        float precioTotal = precioUnitario * nuevaCantidad + importe;

                        model.setValueAt(nuevaCantidad, i, 2); // Actualiza cantidad
                        model.setValueAt(precioTotal, i, 5);   // Actualiza precio total
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    int cantidad = 1;
                    float precioUnitario = producto.getUnitPrice();
                    float importe = producto.getImporte();
                    float precioTotal = precioUnitario * cantidad + importe;

                    model.addRow(new Object[]{
                        producto.getId(),
                        producto.getDescription(),
                        cantidad,
                        importe,
                        precioUnitario,
                        precioTotal
                    });
                }
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID de producto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        codProducto.setText("");
        // <<<<<<<<<<<<<<<<<<<<<<<<<<< AGREGA ESTA LÍNEA AL FINAL >>>>>>>>>>>>>>>>>>>>>>>>>>
        ventasManager.calcularTotal();
    }//GEN-LAST:event_agregarProductoButtonActionPerformed

    private void productosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productosButtonActionPerformed
         ventasPanel.setVisible(false);
         inventarioPanel.setVisible(false);
         productosPanel.setVisible(true);
         cortePanel.setVisible(false);
         historialPanel.setVisible(false);
    }//GEN-LAST:event_productosButtonActionPerformed

    private void inventarioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inventarioButtonActionPerformed
       productosPanel.setVisible(false);
       ventasPanel.setVisible(false);
       inventarioPanel.setVisible(true);
        cortePanel.setVisible(false);
        historialPanel.setVisible(false);
    }//GEN-LAST:event_inventarioButtonActionPerformed

    private void aceptarBusquedaInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarBusquedaInventarioActionPerformed
        String textoBusqueda = buscarProductoInventario.getText().trim().toLowerCase();

        // ---- Para productos normales (tablaInventarioGFV) ----
        DefaultTableModel modeloGFV = (DefaultTableModel) tablaInventarioGFV.getModel();
        modeloGFV.setRowCount(0);
        List<Producto> productos = operacionesBD.buscarProductosPorDescripcion(textoBusqueda);
        for (Producto p : productos) {
            modeloGFV.addRow(new Object[]{
                p.getId(),
                p.getDescription(),
                p.getUnitPrice(),
                p.getAmount(),
                p.getCategory(),
                p.getDiscount(),
                p.getImporte(),
                p.getMinimum()
            });
        }

        // ---- Para productos granel/frutas/verduras (tablaInventario) ----
        DefaultTableModel modeloGranel = (DefaultTableModel) tablaInventario.getModel();
        modeloGranel.setRowCount(0);
        List<mscluna.com.app.mvc.model.ProductoGranel> productosGranel = operacionesBD.buscarProductosGranelPorDescripcion(textoBusqueda);
        for (mscluna.com.app.mvc.model.ProductoGranel g : productosGranel) {
            modeloGranel.addRow(new Object[]{
                g.getIdGProducts(),
                g.getDescriptionGranel(),
                g.getPrizeForKg(),
                g.getAmountForKg(),
                g.getMinimumAmount(),
                g.getPrizeForUnit(),
                g.getCategoryg()
            });
        }
    }//GEN-LAST:event_aceptarBusquedaInventarioActionPerformed

    private void actualizarTablaInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarTablaInventarioActionPerformed
        cargarInventarioATabla();
    }//GEN-LAST:event_actualizarTablaInventarioActionPerformed

    private void cobrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cobrarButtonActionPerformed
        String ventasJson = ventasManager.getVentasComoJson();

        try {
            String baseDir = "C:\\Users\\luiis\\OneDrive\\Escritorio\\JSONS";
            File dir = new File(baseDir);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "venta_" + timestamp + ".json";
            File file = new File(dir, fileName);

            try (FileWriter fw = new FileWriter(file)) {
                fw.write(ventasJson);
            }
            JOptionPane.showMessageDialog(this, "Venta realizada exitosamente.\nArchivo guardado en:\n" + file.getAbsolutePath(), "Venta", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo de venta:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        ventasManager.descontarProductosEnBD(operacionesBD);
        double total = ventasManager.calcularTotal();
        ventasManager.registrarVentaEnBD(operacionesBD, total); // SOLO AQUÍ
        totalVentasTurno += total;
        actualizaLabelsTotales();
        entradasDeDinero.setText(String.format("%.2f", totalVentasTurno));
        try {
            double pagaCon = Double.parseDouble(cambioPagar.getText().trim());
            ventasManager.calcularCambio(pagaCon);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto de pago inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String tipo = (String) model.getValueAt(i, 6);
            if ("varios".equals(tipo)) {
                String descripcion = (String) model.getValueAt(i, 1);
                float cantidad = Float.parseFloat(model.getValueAt(i, 2).toString());
                float totalvario = Float.parseFloat(model.getValueAt(i, 5).toString());
                operacionesBD.insertarVarios(descripcion, (float)cantidad, (float)total);
            }
        }
        ventasManager.limpiarTablaVentas();
        cambioPagar.setText("");
        cargarHistorialATabla();
        cobrarButton.setEnabled(false);
    }//GEN-LAST:event_cobrarButtonActionPerformed

    private void buscarProductoVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarProductoVentaActionPerformed
    buscarProductos ventanaBuscar = new buscarProductos(this, operacionesBD);
    ventanaBuscar.setVisible(true);
    }//GEN-LAST:event_buscarProductoVentaActionPerformed

    private void eliminarProductoCategoriaGFVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProductoCategoriaGFVActionPerformed
        eliminarProducto eliminarButton = new eliminarProducto();
        eliminarButton.setVisible(true);
    }//GEN-LAST:event_eliminarProductoCategoriaGFVActionPerformed

    private void editarProdCategoriaGFVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarProdCategoriaGFVActionPerformed
        editarProductoGFV editarProd = new editarProductoGFV();
        editarProd.setVisible(true);
    }//GEN-LAST:event_editarProdCategoriaGFVActionPerformed

    private void aceptarEntradaProductoGFVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarEntradaProductoGFVActionPerformed
        String id = frmCodigoIngresarGFV.getText().trim();
        String descripcion = txtDescIngresarGFV.getText().trim();

        if (id.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos el código y la descripción.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float precioPorKg = 0f;
        float cantidadPorKg = 0f;
        float minimoPorKg = 0f;
        float precioUnitario = 0f;
        String categoria = "";

        try {
            if (!frmPrecioPorKgGFV.getText().trim().isEmpty()) {
                precioPorKg = Float.parseFloat(frmPrecioPorKgGFV.getText().trim());
            }
            if (!frmExistenciasIngresarGFV.getText().trim().isEmpty()) {
                cantidadPorKg = Float.parseFloat(frmExistenciasIngresarGFV.getText().trim());
            }
            if (!frmMinimoIngresarGFV.getText().trim().isEmpty()) {
                minimoPorKg = Float.parseFloat(frmMinimoIngresarGFV.getText().trim());
            }
            if (!frmPrecioUnitarioIngresarGFV.getText().trim().isEmpty()) {
                precioUnitario = Float.parseFloat(frmPrecioUnitarioIngresarGFV.getText().trim());
            }
            if (!txtCategoriaIngresarGFV.getText().trim().isEmpty()) {
                categoria = txtCategoriaIngresarGFV.getText().trim();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifica los valores numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mscluna.com.app.mvc.model.ProductoGranel productoGranel = new mscluna.com.app.mvc.model.ProductoGranel(
            id,
            descripcion,
            precioPorKg,
            cantidadPorKg,
            minimoPorKg,
            precioUnitario,
            categoria
        );

        boolean exito = operacionesBD.insertarProductoGranel(productoGranel);
        if (exito) {
            JOptionPane.showMessageDialog(this, "Producto granel agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            // Limpiar campos
            frmCodigoIngresarGFV.setText("");
            txtDescIngresarGFV.setText("");
            frmPrecioPorKgGFV.setText("");
            frmExistenciasIngresarGFV.setText("");
            frmMinimoIngresarGFV.setText("");
            frmPrecioUnitarioIngresarGFV.setText("");
            txtCategoriaIngresarGFV.setText("");
            // ACTUALIZA TABLAS DE INVENTARIO:
            cargarInventarioATabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al agregar el producto granel.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_aceptarEntradaProductoGFVActionPerformed

    private void aceptarEntradaProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarEntradaProductoActionPerformed
        try {
            String id = frmCodigoIngresar.getText().trim();
            String descripcion = txtDescIngresar.getText().trim();
            float unitPrice = Float.parseFloat(frmPrecioUnitarioIngresar.getText().trim());
            int amount = Integer.parseInt(frmExistenciasIngresar.getText().trim());
            String categoria = txtCategoriaIngresar.getText().trim();
            String descuento = frmDescuentoIngresar.getText().trim();
            float importe = Float.parseFloat(frmImporteIngresar.getText().trim());
            int minimum = Integer.parseInt(frmMinimoIngresar.getText().trim());

            Producto producto = new Producto(id, descripcion, unitPrice, amount, categoria, descuento, importe, minimum);

            boolean exito = operacionesBD.insertarProducto(producto);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                // Limpiar campos
                frmCodigoIngresar.setText("");
                txtDescIngresar.setText("");
                frmPrecioUnitarioIngresar.setText("");
                frmExistenciasIngresar.setText("");
                txtCategoriaIngresar.setText("");
                frmDescuentoIngresar.setText("");
                frmImporteIngresar.setText("");
                frmMinimoIngresar.setText("");
                // ACTUALIZA TABLAS DE INVENTARIO:
                cargarInventarioATabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al agregar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor verifica los valores numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_aceptarEntradaProductoActionPerformed
    private void recalculaTotal() {
        double total = 0.0;
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        int discountColIndex = model.findColumn("Descuento"); // "Descuento" es el precio sabatino
        int unitPriceColIndex = model.findColumn("Precio Unitario");
        int amountColIndex = model.findColumn("Cantidad");
        int importeColIndex = model.findColumn("Importe");
        int aplicaImporteColIndex = model.findColumn("Aplica Importe");

        for (int i = 0; i < model.getRowCount(); i++) {
            double unit_price = Double.parseDouble(model.getValueAt(i, unitPriceColIndex).toString());
            double discount = discountColIndex != -1 && model.getValueAt(i, discountColIndex) != null
                    ? Double.parseDouble(model.getValueAt(i, discountColIndex).toString())
                    : 0.0;
            double amount = Double.parseDouble(model.getValueAt(i, amountColIndex).toString());
            double importe = Double.parseDouble(model.getValueAt(i, importeColIndex).toString());
            boolean aplicaImporte = aplicaImporteColIndex != -1 && model.getValueAt(i, aplicaImporteColIndex) != null
                    ? Boolean.TRUE.equals(model.getValueAt(i, aplicaImporteColIndex))
                    : true;
            double precio = descuentosCheckBox.isSelected() ? discount : unit_price;
            double subtotal = precio * amount;
            if (aplicaImporte) {
                subtotal += importe;
            }
            total += subtotal;
        }
        mostrarTotalLabel.setText(String.format("$%.2f", total));
    }
    private void eliminarProductoCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarProductoCategoriaActionPerformed
        eliminarProducto neweliminar = new eliminarProducto();
        neweliminar.setVisible(true);
    }//GEN-LAST:event_eliminarProductoCategoriaActionPerformed

    private void editarProdCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarProdCategoriaActionPerformed
        editarProducto editProd = new editarProducto();
        editProd.setVisible(true);
    }//GEN-LAST:event_editarProdCategoriaActionPerformed

    private void descuentosCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descuentosCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descuentosCheckBoxActionPerformed

    private void corteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_corteButtonActionPerformed
        cortePanel.setVisible(true);
        ventasPanel.setVisible(false);
        productosPanel.setVisible(false);
        inventarioPanel.setVisible(false);
        historialPanel.setVisible(false);
    }//GEN-LAST:event_corteButtonActionPerformed

    private void historialButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historialButtonActionPerformed
       historialPanel.setVisible(true);
        ventasPanel.setVisible(false);
        productosPanel.setVisible(false);
        inventarioPanel.setVisible(false);
        cortePanel.setVisible(false);
    }//GEN-LAST:event_historialButtonActionPerformed

    private void cerrarTurnoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarTurnoButtonActionPerformed
            totalVentasTurno = 0.0;
            dineroInicialTurno = 0.0;
            actualizaLabelsTotales();
            // Si tienes paneles que limpiar o resetear, hazlo aquí
            // Regresar al pre-lobby:
            this.setVisible(false);
            preLobby pre = new preLobby();
            pre.setVisible(true);
            this.dispose();
    }//GEN-LAST:event_cerrarTurnoButtonActionPerformed

    private void cortarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cortarButtonActionPerformed
        double dineroEsperadoEnCaja = dineroInicialTurno + totalVentasTurno;
        double dineroEnCajaActual = 0.0;
        try {
            dineroEnCajaActual = Double.parseDouble(dineroEnCaja.getText().trim());
        } catch (NumberFormatException e) {
            dineroEnCajaActual = 0.0;
        }
        double montoCorte = dineroEnCajaActual - dineroEsperadoEnCaja;

        // Mostrar resumen
        String mensaje = "Total ventas del turno: $" + String.format("%.2f", totalVentasTurno) +
                         "\nDinero inicial: $" + String.format("%.2f", dineroInicialTurno) +
                         "\nDinero esperado en caja: $" + String.format("%.2f", dineroEsperadoEnCaja) +
                         "\nDinero contado en caja: $" + String.format("%.2f", dineroEnCajaActual) +
                         "\nMonto del corte: $" + String.format("%.2f", montoCorte);
        JOptionPane.showMessageDialog(this, mensaje, "Resumen Corte de Caja", JOptionPane.INFORMATION_MESSAGE);

        // Guardar en la base de datos cash_cuts
        String cashier = cajero;
        String day_shift = turno;
        String date_cut = java.time.LocalDate.now().toString();
        String hour_cut = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        double cut = totalVentasTurno;
        double money = dineroEnCajaActual;
        double amount_cut = montoCorte;

        operacionesBD.insertarCorteCaja(cashier, day_shift, date_cut, hour_cut, cut, money, amount_cut);
    }//GEN-LAST:event_cortarButtonActionPerformed

    private void eliminarVentasAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarVentasAllActionPerformed
        operacionesBD.eliminarTodasLasVentas();
    }//GEN-LAST:event_eliminarVentasAllActionPerformed

    private void agregarVariosButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarVariosButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();
        // Inserta fila vacía para editar
        model.addRow(new Object[]{
            "",         // Código
            "",         // Descripción (editable)
            1f,         // Cantidad (editable)
            0f,         // Importe (puedes dejarlo en cero)
            0f,         // Precio Unitario (puedes dejarlo en cero)
            0f,         // Precio Total (editable)
            "varios"    // Tipo
        });
    }//GEN-LAST:event_agregarVariosButtonActionPerformed

    private void historialCortesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_historialCortesButtonActionPerformed
        historialCortes hc = new historialCortes();
        hc.setVisible(true);
    }//GEN-LAST:event_historialCortesButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        configuracion config = new configuracion();
        config.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setText("X"); setMargin(new java.awt.Insets(2,2,2,2)); }
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    class ButtonEditor extends javax.swing.DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("X");
            button.setMargin(new java.awt.Insets(2,2,2,2));
            button.addActionListener(e -> {
                ((DefaultTableModel) tablaVentas.getModel()).removeRow(selectedRow);
                recalculaTotal();
            });
        }
        public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
            selectedRow = row;
            return button;
        }
    }
    /**
     * @param args the command line arguments
     */


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aceptarBusquedaInventario;
    private javax.swing.JButton aceptarEntradaProducto;
    private javax.swing.JButton aceptarEntradaProductoGFV;
    private javax.swing.JButton actualizarTablaInventario;
    private javax.swing.JButton agregarProductoButton;
    private javax.swing.JButton agregarVariosButton;
    private javax.swing.JPanel bgLobby;
    private javax.swing.JTextField buscarProductoInventario;
    private javax.swing.JButton buscarProductoVenta;
    private javax.swing.JLabel cajeroEnTurno;
    private javax.swing.JLabel cambioCliente;
    private javax.swing.JLabel cambioLabelText;
    private javax.swing.JFormattedTextField cambioPagar;
    private javax.swing.JLabel categoriaIngresar;
    private javax.swing.JLabel categoriaIngresarGFV;
    private javax.swing.JButton cerrarTurnoButton;
    private javax.swing.JButton cobrarButton;
    private javax.swing.JLabel codLabelInst1;
    private javax.swing.JFormattedTextField codProducto;
    private javax.swing.JLabel codProductoIngresar;
    private javax.swing.JLabel codProductoIngresarGFV;
    private javax.swing.JButton cortarButton;
    private javax.swing.JButton corteButton;
    private javax.swing.JPanel cortePanel;
    private javax.swing.JLabel descripcionProductoIngresar;
    private javax.swing.JLabel descripcionProductoIngresarGFV;
    private javax.swing.JLabel descuentoIngresar;
    private javax.swing.JCheckBox descuentosCheckBox;
    private javax.swing.JFormattedTextField dineroEnCaja;
    private javax.swing.JLabel dineroEnCajaLabel;
    private javax.swing.JLabel dineroEsperado;
    private javax.swing.JLabel dineroEsperadoLabel;
    private javax.swing.JButton editarProdCategoria;
    private javax.swing.JButton editarProdCategoriaGFV;
    private javax.swing.JButton eliminarProductoCategoria;
    private javax.swing.JButton eliminarProductoCategoriaGFV;
    private javax.swing.JButton eliminarVentasAll;
    private javax.swing.JLabel entradaLabel;
    private javax.swing.JLabel entradasDeDinero;
    private javax.swing.JLabel existenciasIngresar;
    private javax.swing.JLabel existenciasIngresarGFV;
    private javax.swing.JFormattedTextField frmCodigoIngresar;
    private javax.swing.JFormattedTextField frmCodigoIngresarGFV;
    private javax.swing.JFormattedTextField frmDescuentoIngresar;
    private javax.swing.JFormattedTextField frmExistenciasIngresar;
    private javax.swing.JFormattedTextField frmExistenciasIngresarGFV;
    private javax.swing.JFormattedTextField frmImporteIngresar;
    private javax.swing.JFormattedTextField frmMinimoIngresar;
    private javax.swing.JFormattedTextField frmMinimoIngresarGFV;
    private javax.swing.JFormattedTextField frmPrecioPorKgGFV;
    private javax.swing.JFormattedTextField frmPrecioUnitarioIngresar;
    private javax.swing.JFormattedTextField frmPrecioUnitarioIngresarGFV;
    private javax.swing.JButton historialButton;
    private javax.swing.JButton historialCortesButton;
    private javax.swing.JPanel historialPanel;
    private javax.swing.JTable historialTable;
    private javax.swing.JLabel horaTurno;
    private javax.swing.JLabel importeProductoIngresar;
    private javax.swing.JLabel indicadorPagina;
    private javax.swing.JButton inventarioButton;
    private javax.swing.JPanel inventarioPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel labelAccesoBDInventario;
    private javax.swing.JLabel labelBuscarProdInv;
    private javax.swing.JLabel labelEntradaProducto;
    private javax.swing.JLabel labelEntradaProducto1;
    private javax.swing.JLabel leAtiendeLabel;
    private javax.swing.JLabel lunaLabel;
    private javax.swing.JLabel minimoLabel;
    private javax.swing.JLabel minimoPorKgGFV;
    private javax.swing.JLabel mostrarTotalLabel;
    private javax.swing.JLabel mscLabel;
    private javax.swing.JLabel nombreCajeroBg;
    private javax.swing.JLabel pagaConLabel;
    private javax.swing.JLabel precioPorKgIngresarGFV;
    private javax.swing.JLabel precioUnitarioIngresar;
    private javax.swing.JLabel precioUnitarioIngresarGFV;
    private javax.swing.JButton productosButton;
    private javax.swing.JPanel productosCodEntrada1;
    private javax.swing.JPanel productosGranelFrutVerdEntrada2;
    private javax.swing.JPanel productosPanel;
    private javax.swing.JButton settingsButton;
    private javax.swing.JTable tablaInventario;
    private javax.swing.JTable tablaInventarioGFV;
    private javax.swing.JTable tablaVentas;
    private javax.swing.JLabel totalLabel;
    private javax.swing.JTextField txtCategoriaIngresar;
    private javax.swing.JTextField txtCategoriaIngresarGFV;
    private javax.swing.JTextField txtDescIngresar;
    private javax.swing.JTextField txtDescIngresarGFV;
    private javax.swing.JButton ventasButton;
    private javax.swing.JPanel ventasPanel;
    private javax.swing.JLabel welcomeLabel;
    // End of variables declaration//GEN-END:variables
}
