/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mscluna.com.app.mvc.model;

/**
 *
 * @author luiis
 */
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private final Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }
public boolean validarUsuario(String username, String password) {
    try {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE username = ? AND password_hash = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password); // OJO: aquí deberías hacer hash si guardas hash
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return false;
}
    // Insertar un nuevo usuario
    public boolean insertarUsuario(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, nombre," +
                " usar_productos_comunes, aplicar_descuentos, revisar_historial_ventas, cobrar_ticket, eliminar_articulos_venta," +
                " usar_buscador_productos, usar_varios, avalar_productos, manejar_varios_tickets, adm_clientes_avalados," +
                " anadir_productos, eliminar_productos, editar_productos, ver_inventario, administrador, fecha_creacion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getNombre());
            stmt.setBoolean(4, usuario.isUsarProductosComunes());
            stmt.setBoolean(5, usuario.isAplicarDescuentos());
            stmt.setBoolean(6, usuario.isRevisarHistorialVentas());
            stmt.setBoolean(7, usuario.isCobrarTicket());
            stmt.setBoolean(8, usuario.isEliminarArticulosVenta());
            stmt.setBoolean(9, usuario.isUsarBuscadorProductos());
            stmt.setBoolean(10, usuario.isUsarVarios());
            stmt.setBoolean(11, usuario.isAvalarProductos());
            stmt.setBoolean(12, usuario.isManejarVariosTickets());
            stmt.setBoolean(13, usuario.isAdmClientesAvalados());
            stmt.setBoolean(14, usuario.isAnadirProductos());
            stmt.setBoolean(15, usuario.isEliminarProductos());
            stmt.setBoolean(16, usuario.isEditarProductos());
            stmt.setBoolean(17, usuario.isVerInventario());
            stmt.setBoolean(18, usuario.isAdministrador());
            stmt.setTimestamp(19, Timestamp.valueOf(usuario.getFechaCreacion() != null ? usuario.getFechaCreacion() : LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    // Actualizar usuario existente
    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET username=?, password_hash=?, nombre=?," +
                " usar_productos_comunes=?, aplicar_descuentos=?, revisar_historial_ventas=?, cobrar_ticket=?, eliminar_articulos_venta=?," +
                " usar_buscador_productos=?, usar_varios=?, avalar_productos=?, manejar_varios_tickets=?, adm_clientes_avalados=?," +
                " anadir_productos=?, eliminar_productos=?, editar_productos=?, ver_inventario=?, administrador=?" +
                " WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPasswordHash());
            stmt.setString(3, usuario.getNombre());
            stmt.setBoolean(4, usuario.isUsarProductosComunes());
            stmt.setBoolean(5, usuario.isAplicarDescuentos());
            stmt.setBoolean(6, usuario.isRevisarHistorialVentas());
            stmt.setBoolean(7, usuario.isCobrarTicket());
            stmt.setBoolean(8, usuario.isEliminarArticulosVenta());
            stmt.setBoolean(9, usuario.isUsarBuscadorProductos());
            stmt.setBoolean(10, usuario.isUsarVarios());
            stmt.setBoolean(11, usuario.isAvalarProductos());
            stmt.setBoolean(12, usuario.isManejarVariosTickets());
            stmt.setBoolean(13, usuario.isAdmClientesAvalados());
            stmt.setBoolean(14, usuario.isAnadirProductos());
            stmt.setBoolean(15, usuario.isEliminarProductos());
            stmt.setBoolean(16, usuario.isEditarProductos());
            stmt.setBoolean(17, usuario.isVerInventario());
            stmt.setBoolean(18, usuario.isAdministrador());
            stmt.setInt(19, usuario.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    // Obtener usuario por username
    public Usuario obtenerUsuarioPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        }
        return null;
    }

    // Eliminar usuario
    public boolean eliminarUsuario(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Listar todos los usuarios
    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        }
        return lista;
    }

    // Mapear ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setUsarProductosComunes(rs.getBoolean("usar_productos_comunes"));
        usuario.setAplicarDescuentos(rs.getBoolean("aplicar_descuentos"));
        usuario.setRevisarHistorialVentas(rs.getBoolean("revisar_historial_ventas"));
        usuario.setCobrarTicket(rs.getBoolean("cobrar_ticket"));
        usuario.setEliminarArticulosVenta(rs.getBoolean("eliminar_articulos_venta"));
        usuario.setUsarBuscadorProductos(rs.getBoolean("usar_buscador_productos"));
        usuario.setUsarVarios(rs.getBoolean("usar_varios"));
        usuario.setAvalarProductos(rs.getBoolean("avalar_productos"));
        usuario.setManejarVariosTickets(rs.getBoolean("manejar_varios_tickets"));
        usuario.setAdmClientesAvalados(rs.getBoolean("adm_clientes_avalados"));
        usuario.setAnadirProductos(rs.getBoolean("anadir_productos"));
        usuario.setEliminarProductos(rs.getBoolean("eliminar_productos"));
        usuario.setEditarProductos(rs.getBoolean("editar_productos"));
        usuario.setVerInventario(rs.getBoolean("ver_inventario"));
        usuario.setAdministrador(rs.getBoolean("administrador"));
        Timestamp timestamp = rs.getTimestamp("fecha_creacion");
        if (timestamp != null) {
            usuario.setFechaCreacion(timestamp.toLocalDateTime());
        }
        return usuario;
    }
}