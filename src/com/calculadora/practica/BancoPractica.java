package com.calculadora.practica;

import java.util.List;

public final class BancoPractica {
    private BancoPractica() { }
    public static List<EjercicioPractica> ejercicios() {
        return List.of(
            new EjercicioPractica("01 · Potencia", "¿Cuál es la derivada de f(x) = 3x²?",
                List.of("3x", "6x", "6x²", "x³"), 1,
                "Conserva el coeficiente y aplica (xⁿ)' = n·xⁿ⁻¹.",
                "1. El 3 multiplica a la función.\n2. La derivada de x² es 2x.\n3. Multiplica 3·2x.\nResultado: f'(x) = 6x."),
            new EjercicioPractica("02 · Linealidad", "Deriva f(x) = x³ + 2x - 5.",
                List.of("3x² + 2", "3x² - 5", "x² + 2", "3x + 2"), 0,
                "Deriva cada término; la derivada de una constante es cero.",
                "1. (x³)' = 3x².\n2. (2x)' = 2.\n3. (-5)' = 0.\nResultado: 3x² + 2."),
            new EjercicioPractica("03 · Cadena", "Deriva f(x) = sin(2x). Los ángulos están en radianes.",
                List.of("cos(2x)", "-2sin(2x)", "2cos(2x)", "2sin(x)"), 2,
                "Deriva sin(u) y multiplica por la derivada de u = 2x.",
                "1. La función externa es sin(u): su derivada es cos(u).\n2. La interna es u = 2x: u' = 2.\n3. Aplica la cadena: cos(2x)·2.\nResultado: 2cos(2x)."),
            new EjercicioPractica("04 · Producto", "Deriva f(x) = x·exp(x).",
                List.of("exp(x)", "x·exp(x)", "2x·exp(x)", "exp(x) + x·exp(x)"), 3,
                "Usa (uv)' = u'v + uv'. La derivada de exp(x) es exp(x).",
                "1. u = x y v = exp(x).\n2. u' = 1 y v' = exp(x).\n3. Sustituye en u'v + uv'.\nResultado: exp(x) + x·exp(x)."),
            new EjercicioPractica("05 · Cociente", "Deriva f(x) = x/(x + 1).",
                List.of("1/(x + 1)²", "1", "-1/(x + 1)²", "x/(x + 1)²"), 0,
                "Usa (u/v)' = (u'v - uv')/v² y simplifica el numerador.",
                "1. u = x, u' = 1; v = x + 1, v' = 1.\n2. Numerador: (x + 1) - x = 1.\n3. Denominador: (x + 1)².\nResultado: 1/(x + 1)², para x distinto de -1."),
            new EjercicioPractica("06 · Integral", "¿Cuál es una integral indefinida de 2x?",
                List.of("2x² + C", "x² + C", "2 + C", "x + C"), 1,
                "Aumenta el exponente en uno y divide por ese exponente.",
                "1. ∫2x dx = 2·x²/2 + C.\n2. Simplifica 2/2 = 1.\nResultado: x² + C. Al derivarlo recuperas 2x."),
            new EjercicioPractica("07 · Integral definida", "Calcula ∫ de 0 a 1 de 3x² dx.",
                List.of("0", "3", "1", "1/3"), 2,
                "Una antiderivada de 3x² es x³. Evalúa F(1) - F(0).",
                "1. F(x) = x³.\n2. F(1) = 1 y F(0) = 0.\n3. F(1) - F(0) = 1.\nResultado: 1."),
            new EjercicioPractica("08 · Pendiente", "Si f(x) = x², ¿cuál es la pendiente de la tangente en x = 2?",
                List.of("2", "4", "0", "8"), 1,
                "La pendiente en un punto se obtiene evaluando f'(x) en ese punto.",
                "1. Deriva f(x) = x²: f'(x) = 2x.\n2. Sustituye x = 2.\nResultado: pendiente = f'(2) = 4."),
            new EjercicioPractica("09 · Área con signo", "¿Cuánto vale ∫ de -1 a 1 de x dx?",
                List.of("2", "1", "-1", "0"), 3,
                "Las contribuciones a cada lado del origen tienen signos opuestos.",
                "1. F(x) = x²/2.\n2. F(1) = 1/2 y F(-1) = 1/2.\n3. Resta: 1/2 - 1/2 = 0.\nLa integral con signo es 0; el área geométrica total es 1."),
            new EjercicioPractica("10 · Logaritmo", "Deriva f(x) = ln(x), para x > 0.",
                List.of("1/x", "ln(x)/x", "x", "exp(x)"), 0,
                "Recuerda la regla básica del logaritmo natural.",
                "Regla: (ln(u))' = u'/u.\nAquí u = x y u' = 1.\nResultado: 1/x, para x > 0.")
        );
    }
}
