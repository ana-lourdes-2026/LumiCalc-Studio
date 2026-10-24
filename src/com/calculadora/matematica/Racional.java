package com.calculadora.matematica;

import com.calculadora.excepciones.ErrorMatematicoException;
import com.calculadora.excepciones.ExpresionInvalidaException;
import java.math.BigDecimal;

/**
 * Fracción exacta (numerador/denominador). Así 1/3 + 1/6 da exactamente 1/2
 * y no 0.4999999... como pasaría con double.
 * Es inmutable: cada operación devuelve un objeto nuevo.
 */
public final class Racional {

    public static final Racional CERO = new Racional(0, 1);
    public static final Racional UNO = new Racional(1, 1);
    public static final Racional MENOS_UNO = new Racional(-1, 1);

    private final long numerador;
    private final long denominador; // siempre positivo

    private Racional(long numerador, long denominador) {
        this.numerador = numerador;
        this.denominador = denominador;
    }

    /** Crea una fracción ya simplificada y con denominador positivo. */
    public static Racional de(long numerador, long denominador) {
        if (denominador == 0) {
            throw new ErrorMatematicoException("División entre cero.");
        }
        try {
            if (numerador == Long.MIN_VALUE) {
                throw new ArithmeticException();
            }
            if (denominador < 0) {
                numerador = Math.negateExact(numerador);
                denominador = Math.negateExact(denominador);
            }
            long g = mcd(Math.abs(numerador), denominador);
            return new Racional(numerador / g, denominador / g);
        } catch (ArithmeticException e) {
            throw desbordamiento();
        }
    }

    /** Convierte texto como "7", "2.5" o "0.75" en fracción exacta (2.5 -> 5/2). */
    public static Racional desdeTexto(String texto) {
        try {
            BigDecimal valor = new BigDecimal(texto);
            int escala = Math.max(valor.scale(), 0);
            if (escala > 15) {
                throw new ExpresionInvalidaException(
                        "El número '" + texto + "' tiene demasiados decimales (máximo 15).");
            }
            long num = valor.movePointRight(escala).toBigIntegerExact().longValueExact();
            long den = 1;
            for (int i = 0; i < escala; i++) {
                den = Math.multiplyExact(den, 10L);
            }
            return de(num, den);
        } catch (ArithmeticException | NumberFormatException e) {
            throw new ExpresionInvalidaException(
                    "El número '" + texto + "' es demasiado grande o no es válido.");
        }
    }

    public Racional sumar(Racional o) {
        try {
            return de(Math.addExact(Math.multiplyExact(numerador, o.denominador),
                                    Math.multiplyExact(o.numerador, denominador)),
                      Math.multiplyExact(denominador, o.denominador));
        } catch (ArithmeticException e) {
            throw desbordamiento();
        }
    }

    public Racional restar(Racional o) {
        return sumar(o.negar());
    }

    public Racional multiplicar(Racional o) {
        try {
            return de(Math.multiplyExact(numerador, o.numerador),
                      Math.multiplyExact(denominador, o.denominador));
        } catch (ArithmeticException e) {
            throw desbordamiento();
        }
    }

    public Racional dividir(Racional o) {
        if (o.esCero()) {
            throw new ErrorMatematicoException("División entre cero.");
        }
        try {
            return de(Math.multiplyExact(numerador, o.denominador),
                      Math.multiplyExact(denominador, o.numerador));
        } catch (ArithmeticException e) {
            throw desbordamiento();
        }
    }

    public Racional negar() {
        return de(-numerador, denominador);
    }

    public boolean esCero() {
        return numerador == 0;
    }

    public boolean esUno() {
        return numerador == 1 && denominador == 1;
    }

    public boolean esEntero() {
        return denominador == 1;
    }

    /** Devuelve -1, 0 o 1 según el signo. */
    public int signo() {
        return Long.signum(numerador);
    }

    public long getNumerador() {
        return numerador;
    }

    public long getDenominador() {
        return denominador;
    }

    public double valorDecimal() {
        return (double) numerador / denominador;
    }

    private static long mcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    private static ErrorMatematicoException desbordamiento() {
        return new ErrorMatematicoException(
                "Un número es demasiado grande para operar con exactitud.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Racional)) {
            return false;
        }
        Racional otro = (Racional) o;
        return numerador == otro.numerador && denominador == otro.denominador;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(numerador) * 31 + Long.hashCode(denominador);
    }

    @Override
    public String toString() {
        return denominador == 1 ? String.valueOf(numerador) : numerador + "/" + denominador;
    }
}
