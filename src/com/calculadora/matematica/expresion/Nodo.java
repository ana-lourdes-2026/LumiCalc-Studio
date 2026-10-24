package com.calculadora.matematica.expresion;

/**
 * Clase base del árbol de expresión. Cada fórmula se guarda como un árbol:
 * 3*x^2 + 1 es una suma cuyo hijo izquierdo es 3*x^2 y el derecho es 1.
 */
public abstract class Nodo {

    /** Calcula el valor numérico de la expresión sustituyendo la variable por "valor". */
    public abstract double evaluar(double valor);

    /**
     * Prioridad para decidir paréntesis al imprimir:
     * 1 = suma/resta, 2 = multiplicación/división, 3 = potencia, 4 = elemento simple.
     */
    public int precedencia() {
        return 4;
    }

    /** Indica si el nodo se escribe con un signo menos al inicio (-3, -x). */
    public boolean esNegativo() {
        return false;
    }
}
