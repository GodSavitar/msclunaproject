package mscluna.com.app.mvc.controller;

import mscluna.com.app.mvc.model.Producto;
import mscluna.com.app.mvc.model.ProductoGranel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD para productos y ventas del punto de venta.
 * Cada método abre y cierra su propia conexión, ¡nunca se reutiliza!
 */
public class OperacionesBD {
    private final String user;
    private final String password;
    private final String database;

    public OperacionesBD(String user, String password, String database) {
        this.user = user;
        this.password = password;
        this.database = database;
    }

    private Connection getConnection() throws SQLException {
        return ConexionBD.getConexion(user, password, database);
    }

    // ----------- PRODUCTOS NORMALES -----------

    public List<Producto> obtenerTodosProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return productos;
    }

    public void insertarCorteCaja(String cashier, String day_shift, String date_cut, String hour_cut, double cut, double money, double amount_cut) {
        String sql = "INSERT INTO cash_cuts (cashier, day_shift, date_cut, hour_cut, cut, money, amount_cut) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cashier);
            pstmt.setString(2, day_shift);
            pstmt.setString(3, date_cut);
            pstmt.setString(4, hour_cut);
            pstmt.setDouble(5, cut);
            pstmt.setDouble(6, money);
            pstmt.setDouble(7, amount_cut);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar corte de caja: " + e.getMessage());
        }
    }

    public Producto obtenerProductoPorId(String id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO products (id, description, unit_price, amount, category, discount, importe, minimum, prizeCost, prizeWholesale) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getId());
            pstmt.setString(2, producto.getDescription());
            pstmt.setFloat(3, producto.getUnitPrice());
            pstmt.setInt(4, producto.getAmount());
            pstmt.setString(5, producto.getCategory());
            pstmt.setString(6, producto.getDiscount());
            pstmt.setFloat(7, producto.getImporte());
            pstmt.setInt(8, producto.getMinimum());
            pstmt.setFloat(9, producto.getPrizeCost());
            pstmt.setFloat(10, producto.getPrizeWholesale());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarProducto(Producto producto) {
        String sql = "UPDATE products SET description = ?, unit_price = ?, amount = ?, category = ?, discount = ?, importe = ?, minimum = ?, prizeCost = ?, prizeWholesale = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getDescription());
            pstmt.setFloat(2, producto.getUnitPrice());
            pstmt.setInt(3, producto.getAmount());
            pstmt.setString(4, producto.getCategory());
            pstmt.setString(5, producto.getDiscount());
            pstmt.setFloat(6, producto.getImporte());
            pstmt.setInt(7, producto.getMinimum());  
            pstmt.setFloat(8, producto.getPrizeCost());
            pstmt.setFloat(9, producto.getPrizeWholesale());
            pstmt.setString(10, producto.getId());
            int afectados = pstmt.executeUpdate();
            return afectados > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar producto: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProducto(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int afectados = pstmt.executeUpdate();
            return afectados > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> buscarProductosPorDescripcion(String descripcion) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE description LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + descripcion + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por descripción: " + e.getMessage());
        }
        return productos;
    }

    public List<Producto> buscarProductosPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + categoria + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por categoría: " + e.getMessage());
        }
        return productos;
    }

    public String obtenerDescripcionProductoPorId(String id) {
        String sql = "SELECT description FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("description");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener descripción del producto: " + e.getMessage());
        }
        return null;
    }

    public void descontarInventarioProducto(String idProducto, float cantidad) {
        String sql = "UPDATE products SET amount = amount - ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, cantidad);
            pstmt.setString(2, idProducto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al descontar inventario para producto " + idProducto + ": " + e.getMessage());
        }
    }

    // ----------- PRODUCTOS GRANEL -----------

    public List<ProductoGranel> obtenerTodosProductosGranel() {
        List<ProductoGranel> productos = new ArrayList<>();
        String sql = "SELECT * FROM g_products";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapearProductoGranel(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos granel: " + e.getMessage());
        }
        return productos;
    }

    public ProductoGranel obtenerProductoGranelPorId(String id) {
        String sql = "SELECT * FROM g_products WHERE id_gproducts = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProductoGranel(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto granel por ID: " + e.getMessage());
        }
        return null;
    }


    public boolean insertarProductoGranel(ProductoGranel producto) {
        String sql = "INSERT INTO g_products (id_gproducts, descriptionGranel, prizeForKg, amountForKg, minimumAmount, prizeForUnit, categoryg, prizeCost, prizeWholesale) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getIdGProducts());
            pstmt.setString(2, producto.getDescriptionGranel());
            pstmt.setFloat(3, producto.getPrizeForKg());
            pstmt.setFloat(4, producto.getAmountForKg());
            pstmt.setFloat(5, producto.getMinimumAmount());
            pstmt.setFloat(6, producto.getPrizeForUnit());
            pstmt.setString(7, producto.getCategoryg());
            pstmt.setFloat(8, producto.getPrizeCost());
            pstmt.setFloat(9, producto.getPrizeWholesale());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto granel: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarProductoGranel(ProductoGranel producto) {
        String sql = "UPDATE g_products SET descriptionGranel = ?, prizeForKg = ?, amountForKg = ?, minimumAmount = ?, prizeForUnit = ?, categoryg = ?, prizeCost = ?, prizeWholesale = ? WHERE id_gproducts = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, producto.getDescriptionGranel());
            pstmt.setFloat(2, producto.getPrizeForKg());
            pstmt.setFloat(3, producto.getAmountForKg());
            pstmt.setFloat(4, producto.getMinimumAmount());
            pstmt.setFloat(5, producto.getPrizeForUnit());
            pstmt.setString(6, producto.getCategoryg());
            pstmt.setFloat(7, producto.getPrizeCost());
            pstmt.setFloat(8, producto.getPrizeWholesale());
            pstmt.setString(9, producto.getIdGProducts());
            int afectados = pstmt.executeUpdate();
            return afectados > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar producto granel: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarProductoGranel(String id) {
        String sql = "DELETE FROM g_products WHERE id_gproducts = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int afectados = pstmt.executeUpdate();
            return afectados > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto granel: " + e.getMessage());
            return false;
        }
    }

    public List<ProductoGranel> buscarProductosGranelPorDescripcion(String descripcion) {
        List<ProductoGranel> productos = new ArrayList<>();
        String sql = "SELECT * FROM g_products WHERE descriptionGranel LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + descripcion + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProductoGranel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos granel por descripción: " + e.getMessage());
        }
        return productos;
    }

    public List<ProductoGranel> buscarProductosGranelPorCategoria(String categoria) {
        List<ProductoGranel> productos = new ArrayList<>();
        String sql = "SELECT * FROM g_products WHERE categoryg LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + categoria + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProductoGranel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos granel por categoría: " + e.getMessage());
        }
        return productos;
    }

    public void descontarInventarioProductoGranel(String idProducto, float cantidad) {
        String sql = "UPDATE g_products SET amountForKg = amountForKg - ? WHERE id_gproducts = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setFloat(1, cantidad);
            pstmt.setString(2, idProducto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al descontar inventario granel para producto " + idProducto + ": " + e.getMessage());
        }
    }

    // ----------- VENTAS / HISTORIAL -----------

    public void insertarVenta(String fecha, String hora, double totalVenta) {
        String sql = "INSERT INTO sales (dateSale, hourSale, amount) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fecha);
            pstmt.setString(2, hora);
            pstmt.setDouble(3, totalVenta);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
        }
    }

    public List<Object[]> obtenerVentasHistorial() {
        List<Object[]> ventas = new ArrayList<>();
        String sql = "SELECT id, dateSale, hourSale, amount FROM sales";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("dateSale");
                fila[2] = rs.getString("hourSale");
                fila[3] = rs.getFloat("amount");
                ventas.add(fila);
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener historial de ventas: " + ex.getMessage());
        }
        return ventas;
    }

    // ----------- MAPEO DE RESULTSETS -----------

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getString("id"),
            rs.getString("description"),
            rs.getFloat("unit_price"),
            rs.getInt("amount"),
            rs.getString("category"),
            rs.getString("discount"),
            rs.getFloat("importe"),
            rs.getInt("minimum"),
            rs.getFloat("prizeCost"),
            rs.getFloat("prizeWholesale")
        );
    }

    private ProductoGranel mapearProductoGranel(ResultSet rs) throws SQLException {
        return new ProductoGranel(
            rs.getString("id_gproducts"),
            rs.getString("descriptionGranel"),
            rs.getFloat("prizeForKg"),
            rs.getFloat("amountForKg"),
            rs.getFloat("minimumAmount"),
            rs.getFloat("prizeForUnit"),
            rs.getString("categoryg"),
            rs.getFloat("prizeCost"),
            rs.getFloat("prizeWholesale")
        );
    }

    // ----------- ELIMINAR HISTORIAL DE VENTAS Y CORTES DE CAJA -----------

    public void eliminarTodasLasVentas() {
        String sql = "DELETE FROM sales";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int afectados = pstmt.executeUpdate();
            System.out.println("Ventas eliminadas: " + afectados);
        } catch (SQLException e) {
            System.err.println("Error al eliminar todas las ventas: " + e.getMessage());
        }
    }
    
    public void eliminarTodosLosCortesDeCaja() {
        String sql = "DELETE FROM cash_cuts";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int afectados = pstmt.executeUpdate();
            System.out.println("Cortes de caja eliminados: " + afectados);
        } catch (SQLException e) {
            System.err.println("Error al eliminar todos los cortes de caja: " + e.getMessage());
        }
    }

    // ----------- INSERTAR EN TABLA VARIOS -----------

    public void insertarVarios(String descripcion, float cantidad, float total) {
        String sql = "INSERT INTO varios (description, amount, total) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, descripcion);
            ps.setFloat(2, cantidad);
            ps.setFloat(3, total);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar en tabla varios: " + e.getMessage());
        }
    }
 // ---------- OBTENER RUTA JSON ----------------
    public String obtenerRutaJsonPorId(int id) {
    String ruta = null;
    String sql = "SELECT ruta FROM rutas_json WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                ruta = rs.getString("ruta");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener la ruta de rutas_json: " + e.getMessage());
    }
    return ruta;
}
}