package com.calculadora.excepciones;

/** Se lanza cuando la operación es válida pero está fuera del alcance de la calculadora. */
public class OperacionNoSoportadaException extends RuntimeException {

    public OperacionNoSoportadaException(String mensaje) {
        super(mensaje);
    }
}
