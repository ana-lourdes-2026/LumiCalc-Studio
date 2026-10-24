package com.calculadora.matematica.calculo;
import com.calculadora.matematica.expresion.*;
import java.util.*;
/** Algebra polinomial de grado <= 24; rechaza expresiones no polinomiales. */
public final class Polinomios {
 private Polinomios(){}
 private static final int MAX=24;
 public static SortedMap<Integer,Double> extraer(Nodo n){
  if(n instanceof Numero || n instanceof Constante){return unico(0,n.evaluar(0));}
  if(n instanceof Variable)return unico(1,1);
  if(!(n instanceof Operacion))throw new IllegalArgumentException("No es polinomio");
  Operacion o=(Operacion)n;char c=o.getOp();
  if(c=='^'){if(contieneVariable(o.getDer()))throw new IllegalArgumentException("Exponente variable: no es polinomio");SortedMap<Integer,Double> a=extraer(o.getIzq());double ex=o.getDer().evaluar(0);if(!Double.isFinite(ex)||ex<0||ex>MAX||ex!=Math.rint(ex))throw new IllegalArgumentException("Exponente no polinomial");SortedMap<Integer,Double> z=unico(0,1);for(int i=0;i<(int)ex;i++)z=mult(z,a);return z;}
  SortedMap<Integer,Double>a=extraer(o.getIzq()),b=extraer(o.getDer());
  if(c=='+'||c=='-'){SortedMap<Integer,Double>r=new TreeMap<>(a);for(Map.Entry<Integer,Double>e:b.entrySet())r.merge(e.getKey(),(c=='+'?1:-1)*e.getValue(),Double::sum);return r;}
  if(c=='*')return mult(a,b);
  if(c=='/'&&b.size()==1&&b.containsKey(0)&&Math.abs(b.get(0))>1e-12){SortedMap<Integer,Double>r=new TreeMap<>();for(Map.Entry<Integer,Double>e:a.entrySet())r.put(e.getKey(),e.getValue()/b.get(0));return r;}
  throw new IllegalArgumentException("No es polinomio");
 }
 private static boolean contieneVariable(Nodo n){if(n instanceof Variable)return true;if(n instanceof Operacion){Operacion o=(Operacion)n;return contieneVariable(o.getIzq())||contieneVariable(o.getDer());}if(n instanceof Funcion)return contieneVariable(((Funcion)n).getArgumento());return false;}
 private static SortedMap<Integer,Double> unico(int e,double v){SortedMap<Integer,Double>r=new TreeMap<>();r.put(e,v);return r;}
 private static SortedMap<Integer,Double> mult(SortedMap<Integer,Double>a,SortedMap<Integer,Double>b){SortedMap<Integer,Double>r=new TreeMap<>();for(int i:a.keySet())for(int j:b.keySet()){if(i+j>MAX)throw new IllegalArgumentException("Grado demasiado alto");r.merge(i+j,a.get(i)*b.get(j),Double::sum);}return r;}
 public static String derivada(Nodo n,char v){SortedMap<Integer,Double>p=extraer(n),r=new TreeMap<>();for(int e:p.keySet())if(e>0)r.put(e-1,p.get(e)*e);return imprimir(r,v);}
 public static String integral(Nodo n,char v){SortedMap<Integer,Double>p=extraer(n),r=new TreeMap<>();for(int e:p.keySet())r.put(e+1,p.get(e)/(e+1));return imprimir(r,v)+" + C";}
 public static String imprimir(SortedMap<Integer,Double>p,char v){StringBuilder s=new StringBuilder();for(int e:((NavigableMap<Integer,Double>)new TreeMap<>(p)).descendingKeySet()){double a=p.get(e);if(Math.abs(a)<1e-10)continue;boolean neg=a<0;double b=Math.abs(a);if(s.length()>0)s.append(neg?" - ":" + ");else if(neg)s.append("-");if(e==0||Math.abs(b-1)>1e-10){String coef=numero(b);s.append(e>0&&coef.contains("/")?"("+coef+")":coef);}if(e>0){if(e==0||Math.abs(b-1)>1e-10)s.append("");s.append(v);if(e!=1)s.append("^").append(e);}}return s.length()==0?"0":s.toString();}
 public static String numero(double x){if(Math.abs(x-Math.rint(x))<1e-10&&Math.abs(x)<1e12)return Long.toString(Math.round(x));for(int d=2;d<=100;d++){double n=x*d;if(Math.abs(n-Math.rint(n))<1e-9)return Math.round(n)+"/"+d;}return Calculadora.format(x);}
}
