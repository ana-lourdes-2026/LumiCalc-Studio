package com.calculadora.pruebas;
import com.calculadora.matematica.calculo.*;
import com.calculadora.matematica.expresion.*;
import com.calculadora.vista.FormulaMatematica;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
public final class PruebasFracciones {
 public static void main(String[] args)throws Exception{
  comprobar("ln(x^2+1)","2*x/(x^2 + 1)",.7,1.4/1.49);
  comprobar("log(x^2+1)","2*x/((x^2 + 1)*ln(10))",.7,1.4/(1.49*Math.log(10)));
  comprobar("sqrt(x^2+1)","x/sqrt(x^2 + 1)",.7,.7/Math.sqrt(1.49));
  comprobar("x/(x+1)","1/(x + 1)^2",.7,1/Math.pow(1.7,2));
  comprobar("cos(2*x)","(-2)*sin(2*x)",.7,-2*Math.sin(1.4));
  for(String t:new String[]{"x/x","(1/x)*2","1/(1/x)"}){
   Nodo n=Simplificador.simplificar(Calculadora.parse(t,'x'));boolean error=false;try{n.evaluar(0);}catch(RuntimeException ex){error=true;}if(!error)throw new AssertionError("Se amplió el dominio de "+t);
  }
  String[] funciones={"sin(2*x)","ln(x^2+1)","log(x^2+1)","sqrt(x^2+1)","x/(x+1)","x*exp(x)","sin(x)*cos(x)","(x^2+1)/(x^2+2)","exp(sin(x))","2^x"};
  for(String t:funciones){Nodo f=Calculadora.parse(t,'x'),d=Calculadora.derivar(f,'x'),lectura=Calculadora.parse(d.toString(),'x');for(double x:new double[]{.3,.7,1.2}){double h=1e-6,esperado=(f.evaluar(x+h)-f.evaluar(x-h))/(2*h),actual=d.evaluar(x);if(Math.abs(actual-esperado)>1e-5||Math.abs(lectura.evaluar(x)-actual)>1e-9)throw new AssertionError(t+" en "+x);}}
  SwingUtilities.invokeAndWait(()->{FormulaMatematica f=new FormulaMatematica("f'(x) = 2*x/((x^2+1)*ln(10))");f.setForeground(Color.BLACK);f.setSize(620,110);BufferedImage im=new BufferedImage(620,110,BufferedImage.TYPE_INT_RGB);f.paint(im.getGraphics());if(f.getPreferredSize().height<45)throw new AssertionError("No dibuja la fracción");});
  System.out.println("OK: 5 formas simplificadas, 30 comparaciones con derivación numérica y lectura, 3 dominios y dibujo de fracción.");
 }
 private static void comprobar(String entrada,String esperado,double x,double valor){Nodo d=Calculadora.derivar(Calculadora.parse(entrada,'x'),'x');System.out.println(entrada+" -> "+d);if(!d.toString().equals(esperado))throw new AssertionError(d+" != "+esperado);if(Math.abs(d.evaluar(x)-valor)>1e-10)throw new AssertionError("Valor incorrecto");}
}
