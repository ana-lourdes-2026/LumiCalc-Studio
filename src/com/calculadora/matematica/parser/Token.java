package com.calculadora.matematica.parser;

/** Una "pieza" del texto escrito: un número, una palabra, un operador o un paréntesis. */
public class Token {

    public enum Tipo { NUMERO, IDENTIFICADOR, OPERADOR, PAR_IZQ, PAR_DER, FIN }

    private final Tipo tipo;
    private final String texto;
    private final int posicion; // posición (desde 1) en el texto original

    public Token(Tipo tipo, String texto, int posicion) {
        this.tipo = tipo;
        this.texto = texto;
        this.posicion = posicion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public int getPosicion() {
        return posicion;
    }

    @Override
    public String toString() {
        return tipo + "(" + texto + ")";
    }
}
