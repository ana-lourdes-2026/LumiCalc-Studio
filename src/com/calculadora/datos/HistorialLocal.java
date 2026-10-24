package com.calculadora.datos;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/** Conserva los últimos 100 cálculos en XML, incluso después de cerrar la app. */
public final class HistorialLocal {
    private final Path archivo;
    public HistorialLocal() { this(Paths.get(System.getProperty("user.home"), ".lumicalc", "historial.xml")); }
    public HistorialLocal(Path archivo) { this.archivo = archivo; }
    public synchronized List<RegistroOperacion> listar() throws IOException {
        if (!Files.exists(archivo)) return new ArrayList<>();
        if (Files.size(archivo) > 20_000_000) throw new IOException("El historial supera el tamaño permitido.");
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(archivo)) { p.loadFromXML(in); }
        List<RegistroOperacion> registros = new ArrayList<>();
        try {
            int cantidad = Integer.parseInt(p.getProperty("cantidad", "0"));
            if (cantidad < 0 || cantidad > 100) throw new IllegalArgumentException();
            for (int i = 0; i < cantidad; i++) {
                String k = i + ".";
                registros.add(new RegistroOperacion(p.getProperty(k + "fecha", ""), p.getProperty(k + "tipo", ""),
                    p.getProperty(k + "funcion", ""), p.getProperty(k + "variable", "x").charAt(0),
                    p.getProperty(k + "desde", ""), p.getProperty(k + "hasta", ""), p.getProperty(k + "punto", ""),
                    p.getProperty(k + "resultado", ""), p.getProperty(k + "pasos", "")));
            }
        } catch (RuntimeException ex) { throw new IOException("El historial tiene datos inválidos. El archivo original se conserva.", ex); }
        return registros;
    }
    public synchronized void guardar(RegistroOperacion nuevo) throws IOException {
        List<RegistroOperacion> registros = listar(); registros.add(0, nuevo);
        if (registros.size() > 100) registros = new ArrayList<>(registros.subList(0, 100));
        Properties p = new Properties(); p.setProperty("cantidad", String.valueOf(registros.size()));
        for (int i = 0; i < registros.size(); i++) {
            RegistroOperacion r = registros.get(i); String k = i + ".";
            p.setProperty(k + "fecha", r.fecha()); p.setProperty(k + "tipo", r.tipo()); p.setProperty(k + "funcion", r.funcion());
            p.setProperty(k + "variable", String.valueOf(r.variable())); p.setProperty(k + "desde", r.desde());
            p.setProperty(k + "hasta", r.hasta()); p.setProperty(k + "punto", r.punto());
            p.setProperty(k + "resultado", r.resultado()); p.setProperty(k + "pasos", r.procedimiento());
        }
        Path padre = archivo.toAbsolutePath().getParent(); Files.createDirectories(padre);
        Path temporal = Files.createTempFile(padre, "historial-", ".tmp");
        try {
            try (OutputStream out = Files.newOutputStream(temporal)) { p.storeToXML(out, "Historial LumiCalc", "UTF-8"); }
            try { Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporal); }
    }
}
