package com.calculadora.excepciones;

/** Se lanza cuando el texto escrito por el usuario tiene errores de sintaxis. */
public class ExpresionInvalidaException extends RuntimeException {

    public ExpresionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
