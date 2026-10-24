package com.calculadora.matematica.expresion;

import com.calculadora.excepciones.ExpresionInvalidaException;

/** Constantes especiales: pi y e. */
public class Constante extends Nodo {

    private final String nombre;

    public Constante(String nombre) {
        if (!nombre.equals("pi") && !nombre.equals("e")) {
            throw new ExpresionInvalidaException("Constante desconocida: " + nombre);
        }
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public double evaluar(double x) {
        return nombre.equals("pi") ? Math.PI : Math.E;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Constante && nombre.equals(((Constante) o).nombre);
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
