package com.calculadora.matematica.calculo;
import com.calculadora.matematica.expresion.*;
import com.calculadora.matematica.parser.Parser;
import java.util.Locale;
public class Calculadora {
 public static Nodo parse(String s,char v){return Parser.analizar(s,v);}
 static Nodo n(long x){return new Numero(x);} static Nodo op(char c,Nodo a,Nodo b){return new Operacion(c,a,b);}
 static Nodo mul(Nodo a,Nodo b){return op('*',a,b);} static Nodo add(Nodo a,Nodo b){return op('+',a,b);} static Nodo div(Nodo a,Nodo b){return op('/',a,b);}
 static Nodo fn(String f,Nodo a){return new Funcion(f,a);}
 public static Nodo derivar(Nodo t,char variable){return Simplificador.simplificar(derivarSinSimplificar(t,variable));}
 private static Nodo derivarSinSimplificar(Nodo t,char variable){
  if(t instanceof Numero || t instanceof Constante)return n(0);
  if(t instanceof Variable)return n(((Variable)t).getNombre().equalsIgnoreCase(""+variable)?1:0);
  if(t instanceof Operacion){Operacion o=(Operacion)t;Nodo a=o.getIzq(),b=o.getDer(),da=derivar(a,variable),db=derivar(b,variable);switch(o.getOp()){
   case '+':return add(da,db);case '-':return op('-',da,db);case '*':return add(mul(da,b),mul(a,db));case '/':return div(op('-',mul(da,b),mul(a,db)),op('^',b,n(2)));
   case '^': if(b instanceof Numero && Math.abs(db.evaluar(1))<1e-12)return mul(mul(b,op('^',a,op('-',b,n(1)))),da);
    return mul(op('^',a,b),add(mul(db,fn("ln",a)),div(mul(b,da),a)));
   default:throw new IllegalArgumentException("Operador no soportado");}}
  if(t instanceof Funcion){Funcion f=(Funcion)t;Nodo a=f.getArgumento(),d=derivar(a,variable),r;switch(f.getNombre()){
   case "sin":r=fn("cos",a);break;case "cos":r=mul(n(-1),fn("sin",a));break;
   case "tan":r=div(n(1),op('^',fn("cos",a),n(2)));break;
   case "cot":r=mul(n(-1),div(n(1),op('^',fn("sin",a),n(2))));break;
   case "sec":r=mul(fn("sec",a),fn("tan",a));break;
   case "csc":r=mul(n(-1),mul(fn("csc",a),fn("cot",a)));break;
   case "ln":r=div(n(1),a);break;case "log":r=div(n(1),mul(a,fn("ln",n(10))));break;
   case "exp":r=fn("exp",a);break;case "sqrt":r=div(n(1),mul(n(2),fn("sqrt",a)));break;
   case "asin":r=div(n(1),fn("sqrt",op('-',n(1),op('^',a,n(2)))));break;
   case "acos":r=mul(n(-1),div(n(1),fn("sqrt",op('-',n(1),op('^',a,n(2))))));break;
   case "atan":r=div(n(1),add(n(1),op('^',a,n(2))));break;
   default:throw new IllegalArgumentException("Derivada simbólica no disponible para "+f.getNombre());}
   return mul(r,d);}
  throw new IllegalArgumentException("Expresión no reconocida");
 }
 public static String integralSimbolica(Nodo t,char variable){
  if(t instanceof Numero || t instanceof Constante)return "("+t+")*"+variable;
  if(t instanceof Variable)return variable+"^2/2";
  if(t instanceof Operacion){Operacion o=(Operacion)t;Nodo a=o.getIzq(),b=o.getDer();switch(o.getOp()){
   case '+':return "("+integralSimbolica(a,variable)+") + ("+integralSimbolica(b,variable)+")";
   case '-':return "("+integralSimbolica(a,variable)+") - ("+integralSimbolica(b,variable)+")";
   case '*':if(a instanceof Numero)return "("+a+")*("+integralSimbolica(b,variable)+")";if(b instanceof Numero)return "("+b+")*("+integralSimbolica(a,variable)+")";break;
   case '^':if(a instanceof Variable && b instanceof Numero){double ex=b.evaluar(0);if(Math.abs(ex+1)<1e-12)return "ln(abs("+variable+"))";return ""+variable+"^("+format(ex+1)+")/("+format(ex+1)+")";}break;
   case '/':if(a instanceof Numero && b instanceof Variable)return "("+a+")*ln(abs("+variable+"))";break;
  }}
  if(t instanceof Funcion && ((Funcion)t).getArgumento() instanceof Variable){Funcion f=(Funcion)t;switch(f.getNombre()){
   case "sin":return "-cos("+variable+")";case "cos":return "sin("+variable+")";case "exp":return "exp("+variable+")";}}
  throw new IllegalArgumentException("Integral simbólica no implementada para esta expresión. Prueba polinomios, sin(x), cos(x) o exp(x); también puedes calcular una integral definida numéricamente.");
 }
 public static double definida(Nodo f,double a,double b){if(!Double.isFinite(a)||!Double.isFinite(b))throw new IllegalArgumentException("Límites inválidos");int count=4000;double h=(b-a)/count,sum=safe(f,a)+safe(f,b);for(int i=1;i<count;i++)sum+=(i%2==0?2:4)*safe(f,a+i*h);return sum*h/3;}
 static double safe(Nodo f,double x){double y=f.evaluar(x);if(!Double.isFinite(y))throw new IllegalArgumentException("La función no está definida en el intervalo. Revisa discontinuidades.");return y;}
 public static String format(double x){return String.format(Locale.US,"%.8g",x);}
}
