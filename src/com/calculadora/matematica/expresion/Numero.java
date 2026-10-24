package com.calculadora.matematica.expresion;

import com.calculadora.matematica.Racional;

/** Un número constante, guardado como fracción exacta. */
public class Numero extends Nodo {

    private final Racional valor;

    public Numero(Racional valor) {
        this.valor = valor;
    }

    public Numero(long entero) {
        this.valor = Racional.de(entero, 1);
    }

    public Racional getValor() {
        return valor;
    }

    @Override
    public double evaluar(double x) {
        return valor.valorDecimal();
    }

    @Override
    public int precedencia() {
        if (valor.signo() < 0) {
            return 1;
        }
        if (valor.getDenominador() != 1) {
            return 2;
        }
        return 4;
    }

    @Override
    public boolean esNegativo() {
        return valor.signo() < 0;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Numero && valor.equals(((Numero) o).valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
