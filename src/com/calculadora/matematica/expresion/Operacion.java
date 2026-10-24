package com.calculadora.matematica.expresion;

import com.calculadora.excepciones.ErrorMatematicoException;
import com.calculadora.matematica.Racional;
import java.util.Objects;

/**
 * Operación binaria: + - * / ^ con un hijo izquierdo y uno derecho.
 * El signo menos unario (-x) se guarda como (-1)*x.
 */
public class Operacion extends Nodo {

    private final char op;
    private final Nodo izq;
    private final Nodo der;

    public Operacion(char op, Nodo izq, Nodo der) {
        if ("+-*/^".indexOf(op) < 0) {
            throw new IllegalArgumentException("Operador no válido: " + op);
        }
        this.op = op;
        this.izq = izq;
        this.der = der;
    }

    public char getOp() {
        return op;
    }

    public Nodo getIzq() {
        return izq;
    }

    public Nodo getDer() {
        return der;
    }

    /** Verdadero si la operación representa un menos unario: (-1)*algo. */
    public boolean esMenosUnario() {
        return op == '*'
                && izq instanceof Numero
                && ((Numero) izq).getValor().equals(Racional.MENOS_UNO);
    }

    @Override
    public double evaluar(double valor) {
        double a = izq.evaluar(valor);
        double b = der.evaluar(valor);
        double r;
        switch (op) {
            case '+':
                r = a + b;
                break;
            case '-':
                r = a - b;
                break;
            case '*':
                r = a * b;
                break;
            case '/':
                if (b == 0) {
                    throw new ErrorMatematicoException(
                            "División entre cero al evaluar en " + valor + ".");
                }
                r = a / b;
                break;
            default:
                r = Math.pow(a, b);
                break;
        }
        if (Double.isNaN(r) || Double.isInfinite(r)) {
            throw new ErrorMatematicoException(
                    "La expresión " + this + " no está definida en " + valor + ".");
        }
        return r;
    }

    @Override
    public int precedencia() {
        if (esMenosUnario()) {
            return 1;
        }
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 3;
        }
    }

    @Override
    public boolean esNegativo() {
        return esMenosUnario();
    }

    /** Pone paréntesis al hijo solo cuando son necesarios para no cambiar el significado. */
    private String envolver(Nodo hijo, boolean derecho) {
        int ph = hijo.precedencia();
        boolean parentesis;
        switch (op) {
            case '+':
                parentesis = derecho && hijo.esNegativo();
                break;
            case '-':
                parentesis = derecho && ph <= 1;
                break;
            case '*':
                parentesis = ph < 2;
                break;
            case '/':
                parentesis = derecho ? ph <= 2 : ph < 2;
                break;
            default: // potencia
                parentesis = derecho ? ph < 3 : ph <= 3;
                break;
        }
        String texto = hijo.toString();
        return parentesis ? "(" + texto + ")" : texto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Operacion)) {
            return false;
        }
        Operacion otra = (Operacion) o;
        return op == otra.op && izq.equals(otra.izq) && der.equals(otra.der);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, izq, der);
    }

    @Override
    public String toString() {
        if (esMenosUnario()) {
            String texto = der.toString();
            return der.precedencia() < 3 ? "-(" + texto + ")" : "-" + texto;
        }
        String simbolo = (op == '+' || op == '-') ? " " + op + " " : String.valueOf(op);
        return envolver(izq, false) + simbolo + envolver(der, true);
    }
}
