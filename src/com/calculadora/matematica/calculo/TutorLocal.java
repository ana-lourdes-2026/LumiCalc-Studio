package com.calculadora.matematica.calculo;

import com.calculadora.matematica.expresion.Nodo;

/** Ayuda determinista para expresiones; no necesita API ni interpreta documentos. */
public final class TutorLocal {
    private TutorLocal() { }
    public static String resolver(String entrada, char variable, boolean integral) {
        Nodo n = Calculadora.parse(entrada, variable);
        if (!integral) {
            String resultado;
            try { resultado = Polinomios.derivada(n, variable); }
            catch (IllegalArgumentException ex) { resultado = Calculadora.derivar(n, variable).toString(); }
            return "TUTOR LOCAL · DERIVADA\nFunción: " + n + "\n\n" + DerivadaPasoAPaso.generar(n, variable)
                + "\n\nRESULTADO: f'(" + variable + ") = " + resultado + "\nLa derivada conserva las restricciones de dominio de la función original.";
        }
        String desarrollo;
        try { desarrollo = Calculadora.integralSimbolica(n, variable); }
        catch (IllegalArgumentException ex) { desarrollo = "Expandir el polinomio y aplicar la regla de la potencia término a término."; }
        String resultado;
        try { resultado = Polinomios.integral(n, variable); }
        catch (IllegalArgumentException ex) { resultado = Calculadora.integralSimbolica(n, variable) + " + C"; }
        return "TUTOR LOCAL · INTEGRAL INDEFINIDA\nFunción: " + n
            + "\n\n1. Identifica los términos y aplica linealidad cuando corresponda."
            + "\n2. Usa la regla de potencias o una integral elemental compatible."
            + "\nDesarrollo: " + desarrollo + "\n\n3. Simplifica y agrega la constante C.\nRESULTADO: " + resultado
            + "\n\nEl motor local admite integrales básicas; una técnica avanzada puede no estar implementada.";
    }
}
