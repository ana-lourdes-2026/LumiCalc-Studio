package com.calculadora.matematica.calculo;

import com.calculadora.matematica.expresion.*;
import com.calculadora.matematica.Racional;
import java.util.*;

/** Identidades básicas; no cancela factores que puedan cambiar el dominio. */
public final class Simplificador {
    private Simplificador() { }
    public static Nodo simplificar(Nodo n) {
        if (n instanceof Funcion) {
            Funcion f = (Funcion)n; Nodo a = simplificar(f.getArgumento());
            if (f.getNombre().equals("ln") && a.equals(new Constante("e"))) return new Numero(1);
            return new Funcion(f.getNombre(), a);
        }
        if (!(n instanceof Operacion)) return n;
        Operacion o = (Operacion)n; Nodo a = simplificar(o.getIzq()), b = simplificar(o.getDer());
        char c = o.getOp();
        if (c == '+' && cero(a)) return b;
        if ((c == '+' || c == '-') && cero(b)) return a;
        if (c == '*' && (cero(a) || cero(b))) return new Numero(0);
        if ((c == '*' || c == '/') && uno(b)) return a;
        if (c == '*' && uno(a)) return b;
        if (c == '^' && uno(b)) return a;
        if (c == '^' && cero(b)) return new Numero(1);
        if (a instanceof Numero && b instanceof Numero) {
            Racional x = ((Numero)a).getValor(), y = ((Numero)b).getValor();
            switch (c) {
                case '+': return new Numero(x.sumar(y));
                case '-': return new Numero(x.restar(y));
                case '*': return new Numero(x.multiplicar(y));
                case '/': return new Numero(x.dividir(y));
                default: break;
            }
        }
        if(c=='*'||c=='/')return producto(new Operacion(c,a,b));
        if(c=='+'||c=='-')return suma(new Operacion(c,a,b));
        return new Operacion(c, a, b);
    }
    /** Agrupa productos y fracciones sin cancelar factores variables ni invertir cocientes. */
    private static Nodo producto(Nodo n){
        List<Nodo> arriba=new ArrayList<>(),abajo=new ArrayList<>();
        recoger(n,false,arriba,abajo);
        Racional coef=Racional.UNO;
        List<Nodo> numerador=new ArrayList<>(),denominador=new ArrayList<>();
        for(Nodo f:arriba){if(f instanceof Numero)coef=coef.multiplicar(((Numero)f).getValor());else numerador.add(f);}
        for(Nodo f:abajo){if(f instanceof Numero&&!cero(f))coef=coef.dividir(((Numero)f).getValor());else denominador.add(f);}
        if(coef.getNumerador()!=1||numerador.isEmpty())numerador.add(0,new Numero(coef.getNumerador()));
        if(coef.getDenominador()!=1)denominador.add(0,new Numero(coef.getDenominador()));
        Nodo a=unir(numerador),b=unir(denominador);
        return denominador.isEmpty()?a:new Operacion('/',a,b);
    }
    private static void recoger(Nodo n,boolean debajo,List<Nodo> arriba,List<Nodo> abajo){
        if(n instanceof Operacion o&&o.getOp()=='*'){
            recoger(o.getIzq(),debajo,arriba,abajo);recoger(o.getDer(),debajo,arriba,abajo);
        }else if(!debajo&&n instanceof Operacion o&&o.getOp()=='/'){
            recoger(o.getIzq(),false,arriba,abajo);recoger(o.getDer(),true,arriba,abajo);
        }else (debajo?abajo:arriba).add(n);
    }
    private static Nodo unir(List<Nodo> fs){if(fs.isEmpty())return new Numero(1);Nodo n=fs.get(0);for(int i=1;i<fs.size();i++)n=new Operacion('*',n,fs.get(i));return n;}
    /** Reduce sumas de números y monomios. No elimina expresiones con dominio restringido. */
    private static Nodo suma(Nodo n){
        List<Nodo> terminos=new ArrayList<>();List<Integer> signos=new ArrayList<>();separar(n,1,terminos,signos);
        Map<Nodo,Racional> coeficientes=new LinkedHashMap<>();
        for(int i=0;i<terminos.size();i++){
            Nodo t=terminos.get(i),base=t;Racional coef=Racional.de(signos.get(i),1);
            if(t instanceof Numero k){coef=coef.multiplicar(k.getValor());base=new Numero(1);}
            else if(t instanceof Operacion o&&o.getOp()=='*'&&o.getIzq() instanceof Numero k){coef=coef.multiplicar(k.getValor());base=o.getDer();}
            if(!monomio(base))return n;
            coeficientes.merge(base,coef,Racional::sumar);
        }
        Nodo resultado=null;
        for(Map.Entry<Nodo,Racional> e:coeficientes.entrySet()){
            Racional c=e.getValue();if(c.esCero())continue;boolean negativo=c.signo()<0;Racional magnitud=negativo?c.negar():c;
            Nodo t=uno(e.getKey())?new Numero(magnitud):magnitud.esUno()?e.getKey():new Operacion('*',new Numero(magnitud),e.getKey());
            if(resultado==null)resultado=negativo?new Operacion('*',new Numero(-1),t):t;
            else resultado=new Operacion(negativo?'-':'+',resultado,t);
        }
        return resultado==null?new Numero(0):resultado;
    }
    private static boolean monomio(Nodo n){return n instanceof Variable||uno(n)||n instanceof Operacion o&&o.getOp()=='^'&&o.getIzq() instanceof Variable&&o.getDer() instanceof Numero k&&k.getValor().esEntero()&&k.getValor().signo()>=0;}
    private static void separar(Nodo n,int signo,List<Nodo> ts,List<Integer> ss){if(n instanceof Operacion o&&(o.getOp()=='+'||o.getOp()=='-')){separar(o.getIzq(),signo,ts,ss);separar(o.getDer(),o.getOp()=='-'?-signo:signo,ts,ss);}else{ts.add(n);ss.add(signo);}}
    private static boolean cero(Nodo n) { return n instanceof Numero && ((Numero)n).getValor().esCero(); }
    private static boolean uno(Nodo n) { return n instanceof Numero && ((Numero)n).getValor().esUno(); }
}
