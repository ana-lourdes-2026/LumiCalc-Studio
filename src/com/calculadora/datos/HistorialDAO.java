package com.calculadora.datos;

import java.sql.*;
import java.util.*;
import java.util.prefs.Preferences;

/** Acceso a MySQL con sentencias preparadas y conexiones de vida corta. */
public class HistorialDAO {
    private final Preferences preferencias = Preferences.userNodeForPackage(HistorialDAO.class);
    private String url = System.getProperty("calc.db.url", preferencias.get("url",
        "jdbc:mysql://localhost:3306/calculadora_calculo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=3000&socketTimeout=5000"));
    private String user = System.getProperty("calc.db.user", preferencias.get("usuario", "root"));
    private String pass = System.getProperty("calc.db.password", System.getenv().getOrDefault("LUMICALC_DB_PASSWORD", ""));
    public String getUrl() { return url; }
    public String getUsuario() { return user; }
    public void configurar(String url, String usuario, String clave) {
        if (!url.startsWith("jdbc:mysql://")) throw new IllegalArgumentException("Usa una URL JDBC de MySQL.");
        this.url = url; this.user = usuario; this.pass = clave;
        preferencias.put("url", url); preferencias.put("usuario", usuario);
        // La contraseña permanece únicamente en memoria durante esta sesión.
    }
    private Connection abrir() throws SQLException {
        DriverManager.setLoginTimeout(5);
        return DriverManager.getConnection(url, user, pass);
    }
    public void probar() throws SQLException {
        try (Connection c = abrir(); Statement s = c.createStatement()) { s.executeQuery("SELECT 1").close(); }
    }
    public void guardar(String tipo, String funcion, char variable, Double inferior, Double superior, String resultado, String pasos) throws SQLException {
        String sql = "INSERT INTO operaciones(tipo_operacion,funcion,variable,limite_inferior,limite_superior,resultado,procedimiento) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = abrir(); PreparedStatement q = c.prepareStatement(sql)) {
            q.setString(1, tipo); q.setString(2, funcion); q.setString(3, String.valueOf(variable));
            if (inferior == null) q.setNull(4, Types.DOUBLE); else q.setDouble(4, inferior);
            if (superior == null) q.setNull(5, Types.DOUBLE); else q.setDouble(5, superior);
            q.setString(6, resultado); q.setString(7, pasos); q.executeUpdate();
        }
    }
    public List<RegistroOperacion> registros() throws SQLException {
        List<RegistroOperacion> registros = new ArrayList<>();
        try (Connection c = abrir(); Statement st = c.createStatement(); ResultSet r = st.executeQuery(
            "SELECT fecha_hora,tipo_operacion,funcion,variable,limite_inferior,limite_superior,resultado,procedimiento FROM operaciones ORDER BY id_operacion DESC LIMIT 100")) {
            while (r.next()) registros.add(new RegistroOperacion(r.getString(1), r.getString(2), r.getString(3),
                r.getString(4).charAt(0), r.getString(5), r.getString(6), "", r.getString(7), r.getString(8)));
        }
        return registros;
    }
    public String listar() throws SQLException {
        StringBuilder s = new StringBuilder();
        for (RegistroOperacion r : registros()) s.append(r.fecha()).append(" | ").append(r.tipo()).append(" | ").append(r.funcion()).append(" = ").append(r.resultado()).append('\n');
        return s.length() == 0 ? "Sin registros todavía" : s.toString();
    }
}
