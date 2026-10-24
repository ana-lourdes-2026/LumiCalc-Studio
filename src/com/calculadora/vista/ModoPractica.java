package com.calculadora.vista;

import com.calculadora.practica.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/** Práctica guiada sin internet; cada primera respuesta cuenta una sola vez. */
public class ModoPractica extends JDialog {
    private final List<EjercicioPractica> banco = BancoPractica.ejercicios();
    private final boolean[] respondidos = new boolean[banco.size()];
    private final boolean[] aciertos = new boolean[banco.size()];
    private final boolean[] asistidos = new boolean[banco.size()];
    private final JComboBox<String> selector = new JComboBox<>();
    private final JTextArea pregunta = new JTextArea(), feedback = new JTextArea();
    private final JPanel opciones = new JPanel();
    private final ButtonGroup grupo = new ButtonGroup();
    private final JProgressBar avance = new JProgressBar(0, banco.size());
    private final JLabel marcador = new JLabel();
    private int actual;

    public ModoPractica(JFrame padre) {
        super(padre, "LumiCalc · Aprende practicando", false);
        setSize(800, 680); setMinimumSize(new Dimension(660, 560)); setLocationRelativeTo(padre);
        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBackground(new Color(247,244,251));contenido.setBorder(new EmptyBorder(20, 22, 20, 22)); setContentPane(contenido);
        JPanel cab = new JPanel(new BorderLayout(10, 8));cab.setOpaque(false);
        JLabel titulo = new JLabel("APRENDE PRACTICANDO"); titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        cab.add(titulo, BorderLayout.NORTH);
        for (EjercicioPractica e : banco) selector.addItem(e.titulo());
        cab.add(selector, BorderLayout.CENTER); cab.add(marcador, BorderLayout.SOUTH); contenido.add(cab, BorderLayout.NORTH);
        JPanel centro = new JPanel(new BorderLayout(10, 12));centro.setOpaque(false);opciones.setOpaque(false);
        prepararTexto(pregunta); pregunta.setFont(new Font("SansSerif", Font.BOLD, 20)); pregunta.setRows(3);
        centro.add(pregunta, BorderLayout.NORTH); opciones.setLayout(new GridLayout(4, 1, 8, 8));
        centro.add(opciones, BorderLayout.CENTER); prepararTexto(feedback); feedback.setRows(7);
        centro.add(new JScrollPane(feedback), BorderLayout.SOUTH); contenido.add(centro, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(8, 10));pie.setOpaque(false); JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));botones.setOpaque(false);
        JButton comprobar = new JButton("Comprobar"), pista = new JButton("Pista"), solucion = new JButton("Ver solución"), siguiente = new JButton("Siguiente →"), reiniciar = new JButton("Reiniciar");
        for (JButton b : new JButton[]{comprobar, pista, solucion, siguiente, reiniciar}) { b.setBackground(new Color(233,222,246));botones.add(b); }comprobar.setBackground(new Color(119,79,163));comprobar.setForeground(Color.WHITE);
        pie.add(botones, BorderLayout.NORTH); avance.setStringPainted(true); pie.add(avance, BorderLayout.SOUTH); contenido.add(pie, BorderLayout.SOUTH);
        selector.addActionListener(e -> cargar(selector.getSelectedIndex()));
        comprobar.addActionListener(e -> comprobar());
        pista.addActionListener(e -> { asistidos[actual] = true; feedback.setText("PISTA\n" + banco.get(actual).pista() + "\n\nEste ejercicio se registrará como práctica con ayuda."); });
        solucion.addActionListener(e -> { asistidos[actual] = true; feedback.setText("SOLUCIÓN EXPLICADA\n" + banco.get(actual).explicacion() + "\n\nEste ejercicio se registrará como práctica con ayuda."); });
        siguiente.addActionListener(e -> selector.setSelectedIndex((actual + 1) % banco.size()));
        reiniciar.addActionListener(e -> { java.util.Arrays.fill(respondidos, false); java.util.Arrays.fill(aciertos, false); java.util.Arrays.fill(asistidos, false); cargar(actual); });
        cargar(0);
    }
    private void prepararTexto(JTextArea a) { a.setEditable(false); a.setLineWrap(true); a.setWrapStyleWord(true); a.setFont(new Font("SansSerif", Font.PLAIN, 16)); a.setBorder(new EmptyBorder(10, 10, 10, 10)); }
    private void cargar(int indice) {
        actual = indice; EjercicioPractica e = banco.get(actual); pregunta.setText(e.pregunta());
        grupo.clearSelection(); opciones.removeAll();
        // Retira también los botones anteriores del grupo.
        java.util.Collections.list(grupo.getElements()).forEach(grupo::remove);
        for (int i = 0; i < e.opciones().size(); i++) { JRadioButton b = new JRadioButton(e.opciones().get(i)); b.setOpaque(false);b.setFont(new Font("SansSerif", Font.PLAIN, 18)); b.setActionCommand(String.valueOf(i)); grupo.add(b); opciones.add(b); }
        feedback.setText(respondidos[actual] ? "Ya respondiste este ejercicio. Puedes seguir practicando; la primera respuesta ya quedó registrada." : "Elige una respuesta. Puedes pedir una pista o consultar la solución.");
        actualizar(); opciones.revalidate(); opciones.repaint();
    }
    private void comprobar() {
        if (grupo.getSelection() == null) { feedback.setText("Selecciona una respuesta antes de comprobar."); return; }
        EjercicioPractica e = banco.get(actual); boolean bien = Integer.parseInt(grupo.getSelection().getActionCommand()) == e.correcta();
        if (!respondidos[actual]) { respondidos[actual] = true; aciertos[actual] = bien && !asistidos[actual]; }
        feedback.setText((bien ? "¡Correcto!\n" : "Revisa tu respuesta.\n") + (bien ? e.explicacion() : e.pista() + "\nPuedes volver a intentar o ver la solución.") + "\n\nEl marcador cuenta solo la primera respuesta sin ayuda."); actualizar();
    }
    private void actualizar() {
        int completados = 0, buenos = 0;
        for (int i = 0; i < banco.size(); i++) { if (respondidos[i]) completados++; if (aciertos[i]) buenos++; }
        marcador.setText("Aciertos al primer intento sin ayuda: " + buenos + " / " + banco.size()); avance.setValue(completados); avance.setString(completados + " de " + banco.size() + " ejercicios respondidos");
    }
}
