package com.calculadora.matematica.expresion;

import com.calculadora.excepciones.ErrorMatematicoException;
import com.calculadora.excepciones.ExpresionInvalidaException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** Función de un argumento: sin, cos, tan, cot, sec, csc, asin, acos, atan, ln, log, exp, sqrt. */
public class Funcion extends Nodo {

    private static final List<String> NOMBRES = Arrays.asList(
            "sin", "cos", "tan", "cot", "sec", "csc",
            "asin", "acos", "atan", "ln", "log", "exp", "sqrt");

    private final String nombre;
    private final Nodo argumento;

    public Funcion(String nombre, Nodo argumento) {
        if (!esFuncion(nombre)) {
            throw new ExpresionInvalidaException("Función desconocida: " + nombre);
        }
        this.nombre = nombre;
        this.argumento = argumento;
    }

    public static boolean esFuncion(String nombre) {
        return NOMBRES.contains(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public Nodo getArgumento() {
        return argumento;
    }

    @Override
    public double evaluar(double valor) {
        double a = argumento.evaluar(valor);
        double r;
        switch (nombre) {
            case "sin":
                r = Math.sin(a);
                break;
            case "cos":
                r = Math.cos(a);
                break;
            case "tan":
                r = Math.tan(a);
                break;
            case "cot":
                r = Math.cos(a) / Math.sin(a);
                break;
            case "sec":
                r = 1.0 / Math.cos(a);
                break;
            case "csc":
                r = 1.0 / Math.sin(a);
                break;
            case "asin":
                if (a < -1 || a > 1) {
                    throw fueraDeDominio(a);
                }
                r = Math.asin(a);
                break;
            case "acos":
                if (a < -1 || a > 1) {
                    throw fueraDeDominio(a);
                }
                r = Math.acos(a);
                break;
            case "atan":
                r = Math.atan(a);
                break;
            case "ln":
                if (a <= 0) {
                    throw fueraDeDominio(a);
                }
                r = Math.log(a);
                break;
            case "log":
                if (a <= 0) {
                    throw fueraDeDominio(a);
                }
                r = Math.log10(a);
                break;
            case "exp":
                r = Math.exp(a);
                break;
            default: // sqrt
                if (a < 0) {
                    throw fueraDeDominio(a);
                }
                r = Math.sqrt(a);
                break;
        }
        if (Double.isNaN(r) || Double.isInfinite(r)) {
            throw fueraDeDominio(a);
        }
        return r;
    }

    private ErrorMatematicoException fueraDeDominio(double a) {
        return new ErrorMatematicoException(
                "La función " + nombre + " no está definida para " + a + ".");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Funcion)) {
            return false;
        }
        Funcion otra = (Funcion) o;
        return nombre.equals(otra.nombre) && argumento.equals(otra.argumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, argumento);
    }

    @Override
    public String toString() {
        return nombre + "(" + argumento + ")";
    }
}