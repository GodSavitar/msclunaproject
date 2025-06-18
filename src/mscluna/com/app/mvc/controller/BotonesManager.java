/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mscluna.com.app.mvc.controller;

/**
 *
 * @author luiis
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

public class BotonesManager {
    private List<JButton> botones;

    public BotonesManager(List<JButton> botonesIniciales) {
        this.botones = new ArrayList<>(botonesIniciales);
    }

    public void eliminarYActualizar(JButton botonAEliminar) {
        botones.remove(botonAEliminar);
        for (JButton btn : botones) {
            btn.setBackground(new Color(59,153,97));
            btn.setForeground(Color.WHITE);
        }
    }

    // Si necesitas obtener la lista actual de botones
    public List<JButton> getBotones() {
        return botones;
    }
}