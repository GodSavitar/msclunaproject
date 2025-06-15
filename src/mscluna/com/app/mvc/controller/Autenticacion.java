package mscluna.com.app.mvc.controller;

import java.sql.Connection;

public class Autenticacion {

    public static boolean validarCredenciales(String usuario, String contrasena, String baseDatos) {
        Connection conexion = ConexionBD.getConexion(usuario, contrasena, baseDatos);

        if (conexion != null) {
            try {
                conexion.close(); // Cerramos la conexión si fue válida
            } catch (Exception e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
            return true;
        }

        return false;
    }
}
