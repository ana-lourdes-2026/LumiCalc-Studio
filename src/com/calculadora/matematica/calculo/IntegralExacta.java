package com.calculadora.matematica.calculo;

import com.calculadora.matematica.expresion.Nodo;
import java.math.*;
import java.util.*;

/** Integral definida exacta para polinomios de coeficientes racionales y límites decimales. */
public final class IntegralExacta {
 private IntegralExacta(){}
 private static final class Q {
  final BigInteger n,d;
  Q(BigInteger n,BigInteger d){if(d.signum()==0)throw new ArithmeticException("División por cero");if(d.signum()<0){n=n.negate();d=d.negate();}BigInteger g=n.gcd(d);this.n=n.divide(g);this.d=d.divide(g);}
  static Q of(double x){if(!Double.isFinite(x))throw new IllegalArgumentException("Número no finito");BigDecimal b=BigDecimal.valueOf(x).stripTrailingZeros();return new Q(b.unscaledValue().multiply(BigInteger.TEN.pow(Math.max(0,-b.scale()))),BigInteger.TEN.pow(Math.max(0,b.scale())));}
  Q add(Q x){return new Q(n.multiply(x.d).add(x.n.multiply(d)),d.multiply(x.d));}
  Q mul(Q x){return new Q(n.multiply(x.n),d.multiply(x.d));}
  Q div(int x){return new Q(n,d.multiply(BigInteger.valueOf(x)));}
  Q pow(int x){return new Q(n.pow(x),d.pow(x));}
  String show(){return d.equals(BigInteger.ONE)?n.toString():n+"/"+d;}
  double value(){return new BigDecimal(n).divide(new BigDecimal(d),MathContext.DECIMAL64).doubleValue();}
 }
 public static final class Solucion {
  public final String resultado, procedimiento;
  Solucion(String r,String p){resultado=r;procedimiento=p;}
 }
 public static Solucion resolver(Nodo f,char variable,double desde,double hasta){
  SortedMap<Integer,Double> pol=Polinomios.extraer(f);
  Q a=Q.of(desde),b=Q.of(hasta),total=new Q(BigInteger.ZERO,BigInteger.ONE);
  StringBuilder ant=new StringBuilder(),evaluacion=new StringBuilder();
  for(Map.Entry<Integer,Double> e:pol.entrySet()){
   if(Math.abs(e.getValue())<1e-12)continue;
   int potencia=e.getKey()+1;Q coef=Q.of(e.getValue()).div(potencia);
   if(ant.length()>0)ant.append(" + ");
   ant.append("(").append(coef.show()).append(")").append(variable).append("^").append(potencia);
   Q superior=coef.mul(b.pow(potencia)),inferior=coef.mul(a.pow(potencia));
   total=total.add(superior).add(new Q(inferior.n.negate(),inferior.d));
   if(evaluacion.length()>0)evaluacion.append(" + ");
   evaluacion.append("[").append(superior.show()).append(" - (").append(inferior.show()).append(")]");
  }
  if(ant.length()==0)ant.append("0");
  String decimal=Calculadora.format(total.value());
  String res=total.show()+(total.d.equals(BigInteger.ONE)?"":" ≈ "+decimal);
  String pasos="INTEGRAL DEFINIDA\n∫ de "+a.show()+" a "+b.show()+" de f("+variable+") d"+variable+"\n\n"
    +"PASO 1 · Aplicamos la regla de potencia término a término:\n"
    +"∫ "+variable+"^n d"+variable+" = "+variable+"^(n+1)/(n+1)\n\n"
    +"PASO 2 · Hallamos una antiderivada:\nF("+variable+") = "+ant+"\n\n"
    +"PASO 3 · Evaluamos los límites (teorema fundamental):\n"
    +"[F("+variable+")] de "+a.show()+" a "+b.show()+" = F("+b.show()+") - F("+a.show()+")\n"
    +evaluacion+"\n\n"
    +"PASO 4 · Simplificamos las fracciones:\n"
    +"RESULTADO EXACTO = "+total.show()+"\n"
    +"APROXIMACIÓN DECIMAL = "+decimal;
  return new Solucion(res,pasos);
 }
}
