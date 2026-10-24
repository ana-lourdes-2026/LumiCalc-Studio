package com.calculadora.pruebas;
import com.calculadora.vista.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
public class VerificarTutor {
 static void igual(String esperado,String actual){if(!esperado.equals(actual))throw new AssertionError(actual+" != "+esperado);}
 public static void main(String[] args)throws Exception{
  igual("(d)/(dx)(3x^2) = 3 · 2x^(2-1) = 6x",FormatoRespuestaTutor.texto("\\[\\frac{d}{dx}(3x^2) = 3 \\cdot 2x^{2-1} = 6x\\]"));
  igual("(1)/((x)/(2))",FormatoRespuestaTutor.texto("\\frac{1}{\\frac{x}{2}}"));
  igual("√(x^(2)+1)",FormatoRespuestaTutor.texto("\\sqrt{x^{2}+1}"));
  igual("Nivel 1 · Reglas básicas",FormatoRespuestaTutor.texto("---\n**Nivel 1 · Reglas básicas**"));
  igual("∫ x dx ≈ π",FormatoRespuestaTutor.texto("\\int x dx \\approx \\pi"));
  String seguro=FormatoRespuestaTutor.html("<img src='https://evil.example/a'> & x < 2");if(seguro.contains("<img")||!seguro.contains("&lt;img")||!seguro.contains("&amp;"))throw new AssertionError("HTML no escapado");
  SwingUtilities.invokeAndWait(()->{try{
   JEditorPane p=new JEditorPane("text/html",FormatoRespuestaTutor.html("**Nivel 1 · Reglas básicas**\n\nEjercicio 1\n\\( f(x) = 3x^2 + 2x + 1 \\)\n\nRegla: derivada de una suma y regla de la potencia.\n\nPaso 1: derivar cada término\n\\[\\frac{d}{dx}(3x^2) = 3 \\cdot 2x^{2-1} = 6x\\]\n\\[\\frac{d}{dx}(2x) = 2\\]\n\\[\\frac{d}{dx}(1) = 0\\]\n\nResultado final: f′(x) = 6x + 2"));p.setEditable(false);p.setSize(520,740);BufferedImage im=new BufferedImage(520,740,BufferedImage.TYPE_INT_RGB);p.paint(im.getGraphics());ImageIO.write(im,"png",new File(System.getProperty("java.io.tmpdir"),"tutor_v3.png"));
   StringBuilder digitos=new StringBuilder();TecladoNumerico n=new TecladoNumerico(digitos::append);for(Component c:n.getComponents())((JButton)c).doClick();igual("7894561230.BORRAR",digitos.toString());
  }catch(Exception ex){throw new RuntimeException(ex);}});
  System.out.println("7 comprobaciones de formato, seguridad y teclado correctas; vista de respuesta renderizada.");
 }
}
