package mscluna.com.app.mvc.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    /**
     * Obtiene una conexión a la base de datos MySQL.
     * 
     * @param user     Usuario de la base de datos.
     * @param password Contraseña de la base de datos.
     * @param database Nombre de la base de datos.
     * @return Conexión activa o null si falla.
     */
    public static Connection getConexion(String user, String password, String database) {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/" + database +
                     "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Obtener la conexión
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver JDBC de MySQL.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

}