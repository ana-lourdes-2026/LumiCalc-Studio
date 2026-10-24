package com.calculadora.vista;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Teclado que envía cada número al campo de expresión sin perder el cursor. */
public final class TecladoNumerico extends JPanel {
    public TecladoNumerico(Consumer<String> insertar) {
        super(new GridLayout(4, 3, 7, 7));
        setOpaque(false);
        setPreferredSize(new Dimension(172, 140));
        for (String tecla : new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", "0", ".", "←"}) {
            JButton boton = new BotonColor(tecla,new Color(226,236,255));
            boton.setFont(new Font("SansSerif", Font.BOLD, 16));
            boton.setBackground(tecla.equals("←") ? new Color(255,210,216) : new Color(226,236,255));
            boton.setForeground(new Color(48, 38, 64));
            boton.setBorder(new EmptyBorder(7, 3, 7, 3));
            boton.setFocusable(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.addActionListener(e -> insertar.accept(tecla.equals("←") ? "BORRAR" : tecla));
            add(boton);
        }
    }
}
