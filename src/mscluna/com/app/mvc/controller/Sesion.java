/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mscluna.com.app.mvc.controller;

/**
 *
 * @author luiis
 */
public class Sesion {
    private static String usuario;
    private static String contrasena;
    private static String baseDatos;

    public static void iniciarSesion(String user, String pass, String bd) {
        usuario = user;
        contrasena = pass;
        baseDatos = bd;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getContrasena() {
        return contrasena;
    }

    public static String getBaseDatos() {
        return baseDatos;
    }
}
