package com.calculadora.vista;

import com.calculadora.matematica.calculo.Calculadora;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/** Teclado cientifico integrado. Sin librerias externas ni eval inseguro. */
public class CalculadoraCientifica extends JDialog {
    private final JTextField pantalla = new JTextField();
    private final JLabel mensaje = new JLabel("Escribe una expresión o utiliza los botones");
    private final Consumer<String> enviar;
    private boolean grados = false;
    private final Color fondo = new Color(18, 23, 46);
    private final Color morado = new Color(111, 79, 218);
    private final Color rosa = new Color(226, 64, 151);

    public CalculadoraCientifica(JFrame owner, Consumer<String> enviar) {
        super(owner, "LumiCalc PRO | Calculadora científica", false);
        this.enviar = enviar;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 650);
        setMinimumSize(new Dimension(390, 550));
        setLocationRelativeTo(owner);
        JPanel root = new JPanel(new BorderLayout(10, 12));
        root.setBackground(fondo);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        setContentPane(root);
        JLabel titulo = new JLabel("✦  CALCULADORA CIENTÍFICA");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        pantalla.setFont(new Font("SansSerif", Font.BOLD, 25));
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT);
        pantalla.setText("3*x^2+2*x+1");
        pantalla.setBackground(new Color(32, 42, 73));
        pantalla.setForeground(Color.WHITE);
        pantalla.setCaretColor(Color.WHITE);
        pantalla.setBorder(new EmptyBorder(14, 10, 14, 10));
        mensaje.setForeground(new Color(174, 193, 227));
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(titulo, BorderLayout.NORTH);
        top.add(pantalla, BorderLayout.CENTER);
        top.add(mensaje, BorderLayout.SOUTH);
        root.add(top, BorderLayout.NORTH);

        String[][] teclas = {
            {"7", "8", "9", "/", "C"},
            {"4", "5", "6", "*", "⌫"},
            {"1", "2", "3", "-", "("},
            {"0", ".", "x", "+", ")"},
            {"x²", "xʸ", "√", "π", "e"},
            {"sin", "cos", "tan", "ln", "log"},
            {"exp", "atan", "1/x", "=", "AC"}
        };
        JPanel grid = new JPanel(new GridLayout(teclas.length, 5, 7, 7));
        grid.setOpaque(false);
        for (String[] fila : teclas) for (String t : fila) {
            JButton b = new JButton(t);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setForeground(Color.WHITE);
            b.setBackground(t.equals("=") ? new Color(13, 154, 139) :
                (t.equals("C") || t.equals("AC") ? rosa :
                ("+-*/".contains(t) && t.length() == 1 ? morado : new Color(49, 60, 102))));
            b.setOpaque(true);
            b.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
            b.addActionListener(e -> pulsar(t));
            grid.add(b);
        }
        root.add(grid, BorderLayout.CENTER);
        JPanel abajo = new JPanel(new GridLayout(1, 2, 8, 0));
        abajo.setOpaque(false);
        JButton evaluar = new JButton("= CALCULAR");
        evaluar.addActionListener(e -> calcular());
        JButton enviarBoton = new JButton("↗ ENVIAR A CÁLCULO");
        enviarBoton.addActionListener(e -> {
            String s = pantalla.getText().trim();
            if (s.isEmpty()) { mensaje.setText("Primero escribe una expresión"); return; }
            enviar.accept(s);
            mensaje.setText("Expresión enviada al laboratorio");
        });
        for (JButton b : new JButton[]{evaluar, enviarBoton}) {
            b.setForeground(Color.WHITE); b.setBackground(morado);
            b.setFont(new Font("SansSerif", Font.BOLD, 12));
            b.setFocusPainted(false); abajo.add(b);
        }
        root.add(abajo, BorderLayout.SOUTH);
        pantalla.addActionListener(e -> calcular());
    }

    private void insertar(String s) {
        int inicio = pantalla.getSelectionStart(), fin = pantalla.getSelectionEnd();
        String anterior = pantalla.getText();
        pantalla.setText(anterior.substring(0, inicio) + s + anterior.substring(fin));
        pantalla.setCaretPosition(inicio + s.length());
        pantalla.requestFocusInWindow();
    }
    private void pulsar(String tecla) {
        switch (tecla) {
            case "C": case "AC": pantalla.setText(""); mensaje.setText("Pantalla limpia"); return;
            case "⌫": {
                int p = pantalla.getCaretPosition();
                if (p > 0) { pantalla.setText(pantalla.getText().substring(0,p-1) + pantalla.getText().substring(p)); pantalla.setCaretPosition(p-1); }
                return;
            }
            case "=": calcular(); return;
            case "x²": insertar("^2"); return;
            case "xʸ": insertar("^"); return;
            case "√": insertar("sqrt("); return;
            case "π": insertar("pi"); return;
            case "1/x": insertar("1/("); return;
            case "sin": case "cos": case "tan": case "ln": case "log": case "exp": case "atan": insertar(tecla+"("); return;
            default: insertar(tecla);
        }
    }
    private void calcular() {
        try {
            String s = pantalla.getText().trim();
            if (s.isEmpty()) throw new IllegalArgumentException("Introduce una operación");
            // Solo se evalúan expresiones numéricas. Las que contienen x se envían al laboratorio.
            if (s.matches("(?is).*\\bx\\b.*")) throw new IllegalArgumentException("Para funciones con x usa ENVIAR A CÁLCULO");
            double r = Calculadora.parse(s, 'x').evaluar(0);
            if (!Double.isFinite(r)) throw new IllegalArgumentException("Resultado no definido");
            pantalla.setText(Calculadora.format(r));
            pantalla.setCaretPosition(pantalla.getText().length());
            mensaje.setText("✓ Resultado calculado");
        } catch (Exception ex) { mensaje.setText("⚠ " + ex.getMessage()); }
    }
}
