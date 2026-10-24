package com.calculadora.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/** PDF A4 multipágina sin librerías externas. Rasteriza para conservar símbolos matemáticos. */
public final class ExportadorPDF {
    private static final int ANCHO = 1240, ALTO = 1754, MARGEN = 82, LIMITE = 1620, RENGLON = 34;
    private ExportadorPDF() { }
    public static void guardar(Path archivo, String contenido, BufferedImage grafica) throws IOException {
        List<BufferedImage> paginas = new ArrayList<>();
        BufferedImage pagina = nuevaPagina(paginas.size() + 1); paginas.add(pagina);
        Graphics2D g = pagina.createGraphics(); configurar(g); int y = 200;
        if (grafica != null) {
            int alto = Math.min(450, (int)((ANCHO - 2.0 * MARGEN) * grafica.getHeight() / grafica.getWidth()));
            g.drawImage(grafica, MARGEN, y, ANCHO - 2 * MARGEN, alto, null); y += alto + 52;
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 25));
        List<String> lineas = envolver(contenido, g.getFontMetrics(), ANCHO - MARGEN * 2);
        for (String linea : lineas) {
            if (y + RENGLON > LIMITE) { g.dispose(); pagina = nuevaPagina(paginas.size() + 1); paginas.add(pagina); g = pagina.createGraphics(); configurar(g); g.setFont(new Font("SansSerif", Font.PLAIN, 25)); y = 200; }
            g.setColor(new Color(34, 42, 65)); g.drawString(linea, MARGEN, y); y += RENGLON;
        }
        g.dispose(); escribir(archivo, paginas);
    }
    private static void configurar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
    private static BufferedImage nuevaPagina(int numero) {
        BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics(); configurar(g); g.setColor(Color.WHITE); g.fillRect(0, 0, ANCHO, ALTO);
        g.setColor(new Color(86, 55, 162)); g.fillRect(0, 0, ANCHO, 24);
        g.setFont(new Font("SansSerif", Font.BOLD, 38)); g.drawString("LumiCalc | Solución matemática", MARGEN, 100);
        g.setColor(new Color(98, 105, 124)); g.setFont(new Font("SansSerif", Font.PLAIN, 21));
        g.drawString("Cálculo II · " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), MARGEN, 140);
        g.drawLine(MARGEN, 166, ANCHO - MARGEN, 166); g.drawLine(MARGEN, 1650, ANCHO - MARGEN, 1650);
        g.drawString("Generado por LumiCalc · Página " + numero, MARGEN, 1695); g.dispose(); return imagen;
    }
    private static List<String> envolver(String texto, FontMetrics medidas, int ancho) {
        List<String> lineas = new ArrayList<>();
        for (String original : texto.replace("\t", "    ").split("\\R", -1)) {
            if (original.isEmpty()) { lineas.add(""); continue; }
            String resto = original;
            while (medidas.stringWidth(resto) > ancho) {
                int fin = 1, corte = -1;
                while (fin < resto.length() && medidas.stringWidth(resto.substring(0, fin + 1)) <= ancho) { if (Character.isWhitespace(resto.charAt(fin))) corte = fin; fin++; }
                if (corte > 0) fin = corte;
                lineas.add(resto.substring(0, fin)); resto = resto.substring(fin).stripLeading();
            }
            lineas.add(resto);
        }
        return lineas;
    }
    private static void escribir(Path archivo, List<BufferedImage> paginas) throws IOException {
        ByteArrayOutputStream pdf = new ByteArrayOutputStream(); List<Integer> offsets = new ArrayList<>(); offsets.add(0);
        texto(pdf, "%PDF-1.4\n%âãÏÓ\n"); objeto(pdf, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");
        StringBuilder hijos = new StringBuilder(); for (int i = 0; i < paginas.size(); i++) hijos.append(3 + i * 3).append(" 0 R ");
        objeto(pdf, offsets, 2, "<< /Type /Pages /Count " + paginas.size() + " /Kids [" + hijos + "] >>");
        for (int i = 0; i < paginas.size(); i++) {
            int pagina = 3 + i * 3, imagen = pagina + 1, flujo = pagina + 2;
            objeto(pdf, offsets, pagina, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595.28 841.89] /Resources << /XObject << /Im0 " + imagen + " 0 R >> >> /Contents " + flujo + " 0 R >>");
            BufferedImage img = paginas.get(i); ByteArrayOutputStream comprimido = new ByteArrayOutputStream();
            try (DeflaterOutputStream out = new DeflaterOutputStream(comprimido)) {
                byte[] fila = new byte[img.getWidth() * 3];
                for (int y = 0; y < img.getHeight(); y++) { for (int x = 0; x < img.getWidth(); x++) { int rgb = img.getRGB(x, y), j = x * 3; fila[j] = (byte)(rgb >> 16); fila[j + 1] = (byte)(rgb >> 8); fila[j + 2] = (byte)rgb; } out.write(fila); }
            }
            stream(pdf, offsets, imagen, "/Type /XObject /Subtype /Image /Width " + img.getWidth() + " /Height " + img.getHeight() + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /FlateDecode", comprimido.toByteArray());
            stream(pdf, offsets, flujo, "", "q 595.28 0 0 841.89 0 0 cm /Im0 Do Q".getBytes(StandardCharsets.US_ASCII));
        }
        int xref = pdf.size(); texto(pdf, "xref\n0 " + offsets.size() + "\n0000000000 65535 f \n");
        for (int i = 1; i < offsets.size(); i++) texto(pdf, String.format(java.util.Locale.ROOT, "%010d 00000 n \n", offsets.get(i)));
        texto(pdf, "trailer\n<< /Size " + offsets.size() + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        Path temporal = Files.createTempFile(archivo.toAbsolutePath().getParent(), "lumicalc-", ".tmp");
        try { Files.write(temporal, pdf.toByteArray()); Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING); }
        finally { Files.deleteIfExists(temporal); }
    }
    private static void objeto(ByteArrayOutputStream out, List<Integer> offsets, int numero, String cuerpo) throws IOException { offsets.add(out.size()); texto(out, numero + " 0 obj\n" + cuerpo + "\nendobj\n"); }
    private static void stream(ByteArrayOutputStream out, List<Integer> offsets, int numero, String opciones, byte[] datos) throws IOException { offsets.add(out.size()); texto(out, numero + " 0 obj\n<< " + opciones + " /Length " + datos.length + " >>\nstream\n"); out.write(datos); texto(out, "\nendstream\nendobj\n"); }
    private static void texto(ByteArrayOutputStream out, String texto) throws IOException { out.write(texto.getBytes(StandardCharsets.ISO_8859_1)); }
}
