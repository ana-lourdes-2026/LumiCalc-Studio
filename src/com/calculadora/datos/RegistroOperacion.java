package com.calculadora.datos;

import java.util.Objects;

/** Datos de un cálculo; no contiene ventanas ni credenciales. */
public record RegistroOperacion(String fecha, String tipo, String funcion, char variable,
        String desde, String hasta, String punto, String resultado, String procedimiento) {
    public RegistroOperacion {
        Objects.requireNonNull(fecha); Objects.requireNonNull(tipo); Objects.requireNonNull(funcion);
        desde = Objects.requireNonNullElse(desde, ""); hasta = Objects.requireNonNullElse(hasta, "");
        punto = Objects.requireNonNullElse(punto, ""); resultado = Objects.requireNonNullElse(resultado, "");
        procedimiento = Objects.requireNonNullElse(procedimiento, "");
    }
    public String detalle() { return "FECHA: " + fecha + "\nOPERACIÓN: " + tipo + "\nFUNCIÓN: " + funcion + "\nVARIABLE: " + variable + "\n\n" + procedimiento + "\n\nRESULTADO FINAL: " + resultado; }
}
