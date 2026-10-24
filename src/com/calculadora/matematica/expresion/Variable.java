package com.calculadora.matematica.expresion;

/** La variable de trabajo (x o y). */
public class Variable extends Nodo {

    private final String nombre;

    public Variable(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public double evaluar(double valor) {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Variable && nombre.equals(((Variable) o).nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
