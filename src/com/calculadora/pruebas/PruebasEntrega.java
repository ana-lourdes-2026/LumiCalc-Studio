package com.calculadora.pruebas;

import com.calculadora.datos.*;
import com.calculadora.matematica.calculo.*;
import com.calculadora.matematica.expresion.Nodo;
import com.calculadora.practica.*;
import com.calculadora.util.ExportadorPDF;
import com.calculadora.vista.GraficaPanel;
import java.nio.file.*;
import java.util.*;
import java.awt.image.BufferedImage;

/** Casos conocidos de cálculo, persistencia y exportación. Lanza error ante fallas. */
public final class PruebasEntrega {
    private static int total;
    private static void verificar(boolean condicion, String mensaje) { if (!condicion) throw new AssertionError(mensaje); total++; System.out.println("[OK] " + mensaje); }
    private static void derivada(String f, char v, double x, double esperado) {
        double real = Calculadora.derivar(Calculadora.parse(f, v), v).evaluar(x);
        verificar(Math.abs(real - esperado) < 1e-8, "Derivada de " + f + " en " + x);
    }
    public static void main(String[] args) throws Exception {
        derivada("x^0", 'x', 0, 0); derivada("3*x^2+2*x+1", 'x', 2, 14);
        derivada("sin(2*x)", 'x', 0, 2); derivada("x*exp(x)", 'x', 1, 2*Math.E);
        derivada("x/(x+1)", 'x', 1, .25); derivada("2^x", 'x', 0, Math.log(2));
        derivada("x^x", 'x', 2, 4*(Math.log(2)+1)); derivada("y^3", 'y', 2, 12);
        derivada("ln(x)", 'x', 2, .5); derivada("sqrt(x)", 'x', 4, .25);
        derivada("cot(x)", 'x', Math.PI/4, -2); derivada("sec(x)", 'x', 0, 0);
        try { Polinomios.derivada(Calculadora.parse("2^x", 'x'), 'x'); throw new AssertionError("Exponente variable tratado como polinomio"); }
        catch (IllegalArgumentException bien) { verificar(true, "Exponente variable no se simplifica como polinomio"); }
        verificar(TutorLocal.resolver("sin(2*x)", 'x', false).contains("cadena"), "Tutor local explica la cadena");
        verificar(TutorLocal.resolver("(x+1)^2", 'x', true).contains("+ C"), "Tutor local integra polinomio expandible");
        verificar(TutorLocal.resolver("cos(x)", 'x', true).contains("sin(x) + C"), "Tutor local integra coseno");
        verificar(Math.abs(Calculadora.definida(Calculadora.parse("3*x^2", 'x'),0,1)-1)<1e-9, "Integral definida de 3x² en [0,1]");
        for (EjercicioPractica e:BancoPractica.ejercicios()) verificar(e.opciones().size()==4 && new HashSet<>(e.opciones()).size()==4 && !e.pista().isBlank() && !e.explicacion().isBlank(), "Ejercicio completo: " + e.titulo());
        Path carpeta = Files.createTempDirectory("lumicalc-entrega-");
        HistorialLocal h = new HistorialLocal(carpeta.resolve("historial.xml"));
        RegistroOperacion r = new RegistroOperacion("2026-10-23 10:00", "Derivada", "3*x^2", 'x', "0", "2", "1", "6x", "Regla de potencia: ∫ y f′.");
        h.guardar(r); verificar(new HistorialLocal(carpeta.resolve("historial.xml")).listar().get(0).equals(r), "Historial recupera todos los campos después de reabrir");
        h.guardar(new RegistroOperacion("2", "Evaluar", "y^2", 'y', "-1", "1", "2", "4", "Sustitución"));
        verificar(h.listar().get(0).variable()=='y' && h.listar().size()==2, "Historial mantiene orden y variable");
        GraficaPanel grafica = new GraficaPanel(); grafica.setSize(1000,500); Nodo f=Calculadora.parse("x^2",'x'); grafica.comparar(f,Calculadora.derivar(f,'x'));
        BufferedImage imagen=grafica.capturar(); int rosa=0,verde=0;
        for(int y=0;y<imagen.getHeight();y++)for(int x=0;x<imagen.getWidth();x++){int color=imagen.getRGB(x,y)&0xffffff;if(color==0xff5bae)rosa++;if(color==0x26c5b1)verde++;}
        verificar(rosa>100&&verde>100,"Gráfica contiene función y derivada en colores distintos");
        Path salida=Paths.get(System.getProperty("lumicalc.test.output",carpeta.toString()));Files.createDirectories(salida);
        javax.imageio.ImageIO.write(imagen,"png",salida.resolve("comparacion.png").toFile());
        String texto=TutorLocal.resolver("3*x^2+2*x+1",'x',false)+"\n\n"+"Línea de prueba: integral ∫, derivada f′ y potencia x².\n".repeat(75);
        ExportadorPDF.guardar(salida.resolve("prueba-solucion.pdf"),texto,imagen);
        byte[] pdf=Files.readAllBytes(salida.resolve("prueba-solucion.pdf"));
        verificar(new String(pdf,0,8,java.nio.charset.StandardCharsets.US_ASCII).startsWith("%PDF-1.4"),"PDF generado con cabecera válida");
        Class.forName("com.mysql.cj.jdbc.Driver"); verificar(true,"Conector MySQL incluido y cargable");
        System.out.println("RESULTADO: "+total+" pruebas correctas.");
    }
}
