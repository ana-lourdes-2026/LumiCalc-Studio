package com.calculadora.practica;

import java.util.List;

/** Un ejercicio con respuesta y explicación verificadas. */
public record EjercicioPractica(String titulo, String pregunta, List<String> opciones,
        int correcta, String pista, String explicacion) {
    public EjercicioPractica {
        opciones = List.copyOf(opciones);
        if (correcta < 0 || correcta >= opciones.size()) {
            throw new IllegalArgumentException("Respuesta fuera de las opciones.");
        }
    }
}
