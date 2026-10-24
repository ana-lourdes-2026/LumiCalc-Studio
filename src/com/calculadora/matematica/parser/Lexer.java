package com.calculadora.matematica.parser;

import com.calculadora.excepciones.ExpresionInvalidaException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Analizador léxico: convierte el texto "3x^2+1" en una lista de tokens.
 * Aquí se detectan caracteres no permitidos y números mal escritos.
 */
public final class Lexer {

    private Lexer() {
    }

    public static List<Token> tokenizar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ExpresionInvalidaException(
                    "La función está vacía. Escribe una expresión, por ejemplo: 3x^2 + 1");
        }
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int n = texto.length();

        while (i < n) {
            char c = texto.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }
            int posicion = i + 1;

            if (esDigito(c)) {
                int inicio = i;
                while (i < n && esDigito(texto.charAt(i))) {
                    i++;
                }
                if (i < n && texto.charAt(i) == '.') {
                    i++;
                    if (i >= n || !esDigito(texto.charAt(i))) {
                        throw new ExpresionInvalidaException("Número mal escrito en la posición "
                                + posicion + ": después del punto decimal debe ir un dígito.");
                    }
                    while (i < n && esDigito(texto.charAt(i))) {
                        i++;
                    }
                }
                if (i < n && (texto.charAt(i) == '.' || texto.charAt(i) == ',')) {
                    throw new ExpresionInvalidaException("Número mal escrito en la posición "
                            + posicion + ". Usa un solo punto como separador decimal (por ejemplo 2.5).");
                }
                tokens.add(new Token(Token.Tipo.NUMERO, texto.substring(inicio, i), posicion));
                continue;
            }

            if (esLetra(c)) {
                int inicio = i;
                while (i < n && esLetra(texto.charAt(i))) {
                    i++;
                }
                tokens.add(new Token(Token.Tipo.IDENTIFICADOR,
                        texto.substring(inicio, i).toLowerCase(Locale.ROOT), posicion));
                continue;
            }

            if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                tokens.add(new Token(Token.Tipo.OPERADOR, String.valueOf(c), posicion));
                i++;
                continue;
            }
            if (c == '(') {
                tokens.add(new Token(Token.Tipo.PAR_IZQ, "(", posicion));
                i++;
                continue;
            }
            if (c == ')') {
                tokens.add(new Token(Token.Tipo.PAR_DER, ")", posicion));
                i++;
                continue;
            }
            if (c == ',') {
                throw new ExpresionInvalidaException("Coma no permitida en la posición "
                        + posicion + ". Usa punto para los decimales (por ejemplo 2.5).");
            }
            throw new ExpresionInvalidaException("Carácter no válido '" + c
                    + "' en la posición " + posicion + ".");
        }

        tokens.add(new Token(Token.Tipo.FIN, "fin de la expresión", n + 1));
        return tokens;
    }

    private static boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
