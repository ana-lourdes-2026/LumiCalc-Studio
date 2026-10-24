package com.calculadora.vista;

import com.calculadora.datos.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class VentanaHistorial extends JDialog {
    private final JComboBox<String> fuente = new JComboBox<>(new String[]{"Historial de esta computadora", "Historial MySQL"});
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Fecha", "Operación", "Expresión", "Resultado"}, 0) {
        public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea detalle = new JTextArea();
    private final JLabel estado = new JLabel("Cargando historial...");
    private final JButton recuperar = new JButton("Recuperar ejercicio y solución");
    private List<RegistroOperacion> registros = new ArrayList<>();
    private final HistorialLocal local;
    private final HistorialDAO mysql;
    public VentanaHistorial(JFrame padre, HistorialLocal local, HistorialDAO mysql, Consumer<RegistroOperacion> cargar) {
        super(padre, "LumiCalc · Historial recuperable", false); this.local = local; this.mysql = mysql;
        setSize(950, 650); setLocationRelativeTo(padre);
        JPanel p = new JPanel(new BorderLayout(10, 12)); p.setBorder(new EmptyBorder(16, 16, 16, 16)); setContentPane(p);
        JPanel cab = new JPanel(new BorderLayout(8, 8)); cab.add(fuente, BorderLayout.CENTER); JButton refrescar = new JButton("Actualizar"); cab.add(refrescar, BorderLayout.EAST); p.add(cab, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); tabla.setRowHeight(26); tabla.setAutoCreateRowSorter(true);
        detalle.setEditable(false); detalle.setLineWrap(true); detalle.setWrapStyleWord(true); detalle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tabla), new JScrollPane(detalle)); split.setResizeWeight(.45); p.add(split, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(8, 8)); pie.add(estado, BorderLayout.NORTH); pie.add(recuperar, BorderLayout.EAST); p.add(pie, BorderLayout.SOUTH);
        tabla.getSelectionModel().addListSelectionListener(e -> { RegistroOperacion r = seleccionado(); if (r != null) { detalle.setText(r.detalle()); detalle.setCaretPosition(0); } recuperar.setEnabled(r != null); });
        recuperar.addActionListener(e -> { RegistroOperacion r = seleccionado(); if (r != null) { cargar.accept(r); dispose(); } });
        refrescar.addActionListener(e -> actualizar()); fuente.addActionListener(e -> actualizar()); actualizar();
    }
    private RegistroOperacion seleccionado() {
        int i = tabla.getSelectedRow(); return i < 0 ? null : registros.get(tabla.convertRowIndexToModel(i));
    }
    private void actualizar() {
        fuente.setEnabled(false); recuperar.setEnabled(false); estado.setText("Consultando...");
        final boolean usarMysql = fuente.getSelectedIndex() == 1;
        new SwingWorker<List<RegistroOperacion>, Void>() {
            protected List<RegistroOperacion> doInBackground() throws Exception { return usarMysql ? mysql.registros() : local.listar(); }
            protected void done() {
                fuente.setEnabled(true); modelo.setRowCount(0); detalle.setText(""); registros = new ArrayList<>();
                try { registros = get(); for (RegistroOperacion r : registros) modelo.addRow(new Object[]{r.fecha(), r.tipo(), r.funcion(), r.resultado()}); estado.setText(registros.size() + " cálculos. Selecciona uno para ver sus pasos y recuperarlo."); }
                catch (Exception ex) { estado.setText("No se pudo leer el historial. Revisa la conexión o el archivo local."); detalle.setText(ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage()); }
            }
        }.execute();
    }
}
