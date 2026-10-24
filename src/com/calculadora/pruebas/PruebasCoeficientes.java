package com.calculadora.pruebas;
import com.calculadora.matematica.calculo.*;
import com.calculadora.matematica.expresion.Nodo;
public final class PruebasCoeficientes {
 public static void main(String[] args){
  comprobar("sin(2*x)","2*cos(2*x)",0.37,2*Math.cos(0.74));
  comprobar("sin(3*x)","3*cos(3*x)",0.42,3*Math.cos(1.26));
  comprobar("exp(2*x)","2*exp(2*x)",0.28,2*Math.exp(0.56));
  Nodo n=Simplificador.simplificar(Calculadora.parse("(x+1)*(1/2)",'x'));
  if(Math.abs(n.evaluar(3)-2)>1e-12)throw new AssertionError("Agrupación de fracción incorrecta");
  Nodo original=Calculadora.parse(n.toString(),'x');if(Math.abs(original.evaluar(3)-2)>1e-12)throw new AssertionError("El texto cambió el significado");
  System.out.println("Coeficientes delante: 3 resultados esperados y valores correctos; fracción y agrupación conservadas.");
 }
 private static void comprobar(String entrada,String esperado,double x,double valor){Nodo n=Calculadora.derivar(Calculadora.parse(entrada,'x'),'x');if(!n.toString().equals(esperado))throw new AssertionError(n.toString());if(Math.abs(n.evaluar(x)-valor)>1e-10)throw new AssertionError("Valor incorrecto");}
}
