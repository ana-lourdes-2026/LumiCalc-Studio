package com.calculadora.excepciones;

/** Se lanza ante errores matemáticos: división entre cero, logaritmo de negativos, etc. */
public class ErrorMatematicoException extends RuntimeException {

    public ErrorMatematicoException(String mensaje) {
        super(mensaje);
    }
}