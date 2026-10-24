package com.calculadora.matematica.parser;

import com.calculadora.excepciones.ExpresionInvalidaException;
import com.calculadora.matematica.Racional;
import com.calculadora.matematica.expresion.Constante;
import com.calculadora.matematica.expresion.Funcion;
import com.calculadora.matematica.expresion.Nodo;
import com.calculadora.matematica.expresion.Numero;
import com.calculadora.matematica.expresion.Operacion;
import com.calculadora.matematica.expresion.Variable;
import java.util.List;

/**
 * Analizador sintáctico por descenso recursivo. Cada regla de la gramática es un método:
 *
 *   expresion := termino (('+' | '-') termino)*
 *   termino   := unario (('*' | '/') unario | factor_implicito)*
 *   unario    := ('-' | '+') unario | potencia
 *   potencia  := primario ('^' unario)?
 *   primario  := NUMERO | pi | e | variable | funcion '(' expresion ')' | '(' expresion ')'
 *
 * Los métodos de arriba se ejecutan después que los de abajo, por eso la multiplicación
 * se calcula antes que la suma y la potencia antes que la multiplicación.
 */
public class Parser {

    private final List<Token> tokens;
    private final String variable; // "x" o "y"
    private int actual = 0;

    private Parser(List<Token> tokens, String variable) {
        this.tokens = tokens;
        this.variable = variable;
    }

    /** Convierte el texto en un árbol de expresión usando la variable indicada (X o Y). */
    public static Nodo analizar(String texto, char variable) {
        char v = Character.toLowerCase(variable);
        if (v != 'x' && v != 'y') {
            throw new ExpresionInvalidaException("La variable de trabajo debe ser X o Y.");
        }
        Parser parser = new Parser(Lexer.tokenizar(texto), String.valueOf(v));
        return parser.expresionCompleta();
    }

    private Nodo expresionCompleta() {
        Nodo nodo = expresion();
        Token t = mirar();
        if (t.getTipo() != Token.Tipo.FIN) {
            if (t.getTipo() == Token.Tipo.PAR_DER) {
                throw new ExpresionInvalidaException("Hay un paréntesis de cierre ')' en la posición "
                        + t.getPosicion() + " que no tiene su paréntesis de apertura.");
            }
            throw new ExpresionInvalidaException("No se esperaba '" + t.getTexto()
                    + "' en la posición " + t.getPosicion()
                    + ". ¿Falta un operador (+, -, *, /)?");
        }
        return nodo;
    }

    private Nodo expresion() {
        Nodo izq = termino();
        while (esOperador('+') || esOperador('-')) {
            char op = avanzar().getTexto().charAt(0);
            Nodo der = termino();
            izq = new Operacion(op, izq, der);
        }
        return izq;
    }

    private Nodo termino() {
        Nodo izq = unario();
        while (true) {
            if (esOperador('*') || esOperador('/')) {
                char op = avanzar().getTexto().charAt(0);
                Nodo der = unario();
                izq = new Operacion(op, izq, der);
            } else if (mirar().getTipo() == Token.Tipo.IDENTIFICADOR
                    || mirar().getTipo() == Token.Tipo.PAR_IZQ) {
                // Multiplicación implícita: 2x, 3(x+1), (x+1)(x-1), x sin(x)
                Nodo der = potencia();
                izq = new Operacion('*', izq, der);
            } else {
                break;
            }
        }
        return izq;
    }

    private Nodo unario() {
        if (esOperador('-')) {
            avanzar();
            return negar(unario());
        }
        if (esOperador('+')) {
            avanzar();
            return unario();
        }
        return potencia();
    }

    private Nodo potencia() {
        Nodo base = primario();
        if (esOperador('^')) {
            avanzar();
            Nodo exponente = unario(); // permite x^-2 y es asociativo a la derecha
            return new Operacion('^', base, exponente);
        }
        return base;
    }

    private Nodo primario() {
        Token t = mirar();
        switch (t.getTipo()) {
            case NUMERO:
                avanzar();
                return new Numero(Racional.desdeTexto(t.getTexto()));
            case IDENTIFICADOR:
                return identificador();
            case PAR_IZQ:
                avanzar();
                Nodo interior = expresion();
                if (mirar().getTipo() != Token.Tipo.PAR_DER) {
                    throw new ExpresionInvalidaException(
                            "Falta cerrar el paréntesis abierto en la posición " + t.getPosicion() + ".");
                }
                avanzar();
                return interior;
            case FIN:
                throw new ExpresionInvalidaException("La expresión está incompleta: "
                        + "se esperaba un número, una variable o una función al final.");
            default:
                throw new ExpresionInvalidaException("Se esperaba un número, variable, función o "
                        + "paréntesis, pero se encontró '" + t.getTexto()
                        + "' en la posición " + t.getPosicion() + ".");
        }
    }

    private Nodo identificador() {
        Token t = avanzar();
        String nombre = t.getTexto();

        if (Funcion.esFuncion(nombre)) {
            if (mirar().getTipo() != Token.Tipo.PAR_IZQ) {
                throw new ExpresionInvalidaException("Después de '" + nombre
                        + "' debes abrir un paréntesis, por ejemplo " + nombre + "(" + variable + ").");
            }
            Token apertura = avanzar();
            Nodo argumento = expresion();
            if (mirar().getTipo() != Token.Tipo.PAR_DER) {
                throw new ExpresionInvalidaException("Falta cerrar el paréntesis de la función '"
                        + nombre + "' abierto en la posición " + apertura.getPosicion() + ".");
            }
            avanzar();
            return new Funcion(nombre, argumento);
        }
        if (nombre.equals("pi") || nombre.equals("e")) {
            return new Constante(nombre);
        }
        if (nombre.equals(variable)) {
            return new Variable(nombre);
        }
        if (nombre.equals("x") || nombre.equals("y")) {
            throw new ExpresionInvalidaException("La función contiene la variable '"
                    + nombre.toUpperCase() + "', pero seleccionaste " + variable.toUpperCase()
                    + ". Cambia la variable de trabajo o corrige la función.");
        }
        if (nombre.length() == 1) {
            throw new ExpresionInvalidaException("La letra '" + nombre + "' no es válida. "
                    + "Usa solo la variable " + variable.toUpperCase()
                    + ", las constantes pi y e, o funciones conocidas.");
        }
        throw new ExpresionInvalidaException("'" + nombre + "' no se reconoce como función, "
                + "constante o variable. Si querías multiplicar, usa * (por ejemplo x*pi).");
    }

    /** Menos unario: un número se niega directamente; lo demás se guarda como (-1)*operando. */
    private Nodo negar(Nodo operando) {
        if (operando instanceof Numero) {
            return new Numero(((Numero) operando).getValor().negar());
        }
        return new Operacion('*', new Numero(Racional.MENOS_UNO), operando);
    }

    private Token mirar() {
        return tokens.get(actual);
    }

    private Token avanzar() {
        Token t = tokens.get(actual);
        if (actual < tokens.size() - 1) {
            actual++;
        }
        return t;
    }

    private boolean esOperador(char c) {
        Token t = mirar();
        return t.getTipo() == Token.Tipo.OPERADOR && t.getTexto().charAt(0) == c;
    }
}
