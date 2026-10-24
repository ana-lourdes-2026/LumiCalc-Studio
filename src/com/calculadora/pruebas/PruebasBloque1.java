package com.calculadora.pruebas;

import com.calculadora.excepciones.ErrorMatematicoException;
import com.calculadora.excepciones.ExpresionInvalidaException;
import com.calculadora.matematica.Racional;
import com.calculadora.matematica.calculo.Calculadora;
import com.calculadora.matematica.calculo.DerivadaPasoAPaso;
import com.calculadora.matematica.expresion.Nodo;
import com.calculadora.matematica.parser.Parser;

/**
 * Pruebas con resultados conocidos para el bloque 1.
 * Ejecutar: clic derecho en este archivo -> Run File (Shift+F6).
 */
public class PruebasBloque1 {

    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        System.out.println("=== 1. FRACCIONES EXACTAS (Racional) ===");
        pruebasRacional();
        System.out.println();
        System.out.println("=== 2. ARBOL DE EXPRESION (cómo se interpreta el texto) ===");
        pruebasArbol();
        System.out.println();
        System.out.println("=== 3. EVALUACION NUMERICA (valores conocidos) ===");
        pruebasEvaluacion();
        System.out.println();
        System.out.println("=== 4. ERRORES DE EVALUACION (matemáticos) ===");
        pruebasErroresEvaluacion();
        System.out.println();
        System.out.println("=== 5. ENTRADAS INCORRECTAS (sintaxis) ===");
        pruebasEntradasIncorrectas();
        System.out.println();
        System.out.println("=== 6. DERIVADAS Y EXPLICACIÓN PASO A PASO ===");
        pruebasDerivadas();
        System.out.println();
        System.out.println("RESULTADO: " + correctas + " correctas, " + fallidas + " fallidas.");
    }

    // ---------------------------------------------------------------- Racional

    private static void pruebasRacional() {
        racional("1/2 + 1/3 = 5/6", Racional.de(1, 2).sumar(Racional.de(1, 3)), "5/6");
        racional("3/4 - 1/4 = 1/2", Racional.de(3, 4).restar(Racional.de(1, 4)), "1/2");
        racional("(2/3) * (3/4) = 1/2", Racional.de(2, 3).multiplicar(Racional.de(3, 4)), "1/2");
        racional("(1/2) / (1/4) = 2", Racional.de(1, 2).dividir(Racional.de(1, 4)), "2");
        racional("2/-4 se normaliza a -1/2", Racional.de(2, -4), "-1/2");
        racional("6/3 se simplifica a 2", Racional.de(6, 3), "2");
        racional("texto 0.75 -> 3/4", Racional.desdeTexto("0.75"), "3/4");
        racional("texto 2.50 -> 5/2", Racional.desdeTexto("2.50"), "5/2");
        errorRunnable("1/0 lanza ErrorMatematico", () -> Racional.de(1, 0), ErrorMatematicoException.class);
        errorRunnable("desbordamiento detectado",
                () -> Racional.de(Long.MAX_VALUE, 1).sumar(Racional.UNO), ErrorMatematicoException.class);
    }

    // ------------------------------------------------------------------ Árbol

    private static void pruebasArbol() {
        arbol("3x^2 + 1", 'x', "3*x^2 + 1");
        arbol("x^2 - 2x + 1", 'x', "x^2 - 2*x + 1");
        arbol("-x^2", 'x', "-x^2");
        arbol("-(x+1)", 'x', "-(x + 1)");
        arbol("2*-x", 'x', "2*(-x)");
        arbol("(x+1)(x-1)", 'x', "(x + 1)*(x - 1)");
        arbol("3(x+1)", 'x', "3*(x + 1)");
        arbol("x - (x + 1)", 'x', "x - (x + 1)");
        arbol("1/2 x", 'x', "1/2*x");
        arbol("x/(x+1)", 'x', "x/(x + 1)");
        arbol("sin(2x)", 'x', "sin(2*x)");
        arbol("sin(x)cos(x)", 'x', "sin(x)*cos(x)");
        arbol("2^-3", 'x', "2^(-3)");
        arbol("x^(1/2)", 'x', "x^(1/2)");
        arbol("x^2^3", 'x', "x^2^3");
        arbol("(x+1)^2", 'x', "(x + 1)^2");
        arbol("sqrt(x)", 'x', "sqrt(x)");
        arbol("3X^2", 'X', "3*x^2");
        arbol("y^3 + 2y", 'y', "y^3 + 2*y");
        arbol("2pi", 'x', "2*pi");
        arbol("e^x", 'x', "e^x");
        arbol("2.5x", 'x', "5/2*x");
    }

    // ------------------------------------------------------------- Evaluación

    private static void pruebasEvaluacion() {
        valor("x^2 + 3x - 1", 'x', 2, 9);
        valor("(x+1)(x-1)", 'x', 3, 8);
        valor("-x^2", 'x', 3, -9);
        valor("-2^2", 'x', 0, -4);
        valor("2^-3", 'x', 0, 0.125);
        valor("x^(1/2)", 'x', 4, 2);
        valor("1/2x  (se interpreta como (1/2)*x)", 'x', 4, 2);
        valor("3/2x  (se interpreta como (3/2)*x)", 'x', 4, 6);
        valor("2.5x", 'x', 2, 5);
        valor("y^3 + 2y", 'y', 2, 12);
        valor("sqrt(x)", 'x', 9, 3);
        valor("2pi", 'x', 0, 2 * Math.PI);
        valor("e^x", 'x', 0, 1);
        valor("exp(x)", 'x', 1, Math.E);
        valor("sin(x)", 'x', Math.PI / 2, 1);
        valor("cos(x)", 'x', 0, 1);
        valor("tan(x)", 'x', 0, 0);
        valor("sec(x)", 'x', 0, 1);
        valor("csc(x)", 'x', Math.PI / 2, 1);
        valor("cot(x)", 'x', Math.PI / 4, 1);
        valor("asin(x)", 'x', 1, Math.PI / 2);
        valor("acos(x)", 'x', 1, 0);
        valor("atan(x)", 'x', 1, Math.PI / 4);
        valor("ln(x)", 'x', 1, 0);
        valor("log(x)  (base 10)", 'x', 100, 2);
    }

    private static void pruebasErroresEvaluacion() {
        errorEvaluacion("1/(x-1)", 'x', 1);
        errorEvaluacion("1/0", 'x', 0);
        errorEvaluacion("ln(x)", 'x', -1);
        errorEvaluacion("sqrt(x)", 'x', -4);
        errorEvaluacion("asin(x)", 'x', 2);
        errorEvaluacion("x^(1/2)", 'x', -1);
    }

    // ------------------------------------------------------ Entradas incorrectas

    private static void pruebasEntradasIncorrectas() {
        errorSintaxis("", 'x');
        errorSintaxis("   ", 'x');
        errorSintaxis("3x^", 'x');
        errorSintaxis("x*", 'x');
        errorSintaxis("(x+1", 'x');
        errorSintaxis("x+1)", 'x');
        errorSintaxis("()", 'x');
        errorSintaxis("sin()", 'x');
        errorSintaxis("sin x", 'x');
        errorSintaxis("2 3", 'x');
        errorSintaxis("x^^2", 'x');
        errorSintaxis("3x$", 'x');
        errorSintaxis("1,5x", 'x');
        errorSintaxis("2..3", 'x');
        errorSintaxis("1.2.3", 'x');
        errorSintaxis(".5x", 'x');
        errorSintaxis("foo(x)", 'x');
        errorSintaxis("xy", 'x');
        errorSintaxis("y^2", 'x');     // variable Y con X seleccionada
        errorSintaxis("x^2", 'y');     // variable X con Y seleccionada
        errorSintaxis("99999999999999999999x", 'x');
        errorSintaxis("x", 'z');       // variable de trabajo inválida
    }

    private static void pruebasDerivadas() {
        derivada("x^3 + 2x", 'x', 2, 14);
        derivada("sin(2x)", 'x', 0, 2);
        derivada("sec(x)", 'x', 0, 0);
        derivada("cot(x)", 'x', Math.PI / 4, -2);
        derivada("csc(x)", 'x', Math.PI / 2, 0);
        String producto = DerivadaPasoAPaso.generar(Parser.analizar("x*sin(x)", 'x'), 'x');
        registrar("pasos identifican la regla del producto", producto.contains("Producto:"), producto);
        registrar("pasos identifican la regla de la cadena",
                DerivadaPasoAPaso.generar(Parser.analizar("sin(x^2)", 'x'), 'x').contains("Regla de la cadena"), "no se encontró la regla");
    }

    private static void derivada(String entrada, char var, double x, double esperado) {
        String descripcion = "d/d" + var + "(" + entrada + ") en " + var + "=" + x + " -> " + esperado;
        try {
            Nodo expresion = Parser.analizar(entrada, var);
            double real = Calculadora.derivar(expresion, var).evaluar(x);
            registrar(descripcion, Math.abs(real - esperado) < 1e-8,
                    "esperado " + esperado + " pero fue " + real);
        } catch (RuntimeException e) {
            registrar(descripcion, false, "lanzó error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------ Ayudas de prueba

    private static void registrar(String descripcion, boolean paso, String detalle) {
        if (paso) {
            correctas++;
            System.out.println("  [OK]    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  [FALLA] " + descripcion + "  ->  " + detalle);
        }
    }

    private static void racional(String descripcion, Racional real, String esperado) {
        registrar(descripcion, real.toString().equals(esperado),
                "esperado " + esperado + " pero fue " + real);
    }

    private static void errorRunnable(String descripcion, Runnable accion,
            Class<? extends RuntimeException> tipo) {
        try {
            accion.run();
            registrar(descripcion, false, "no lanzó ningún error");
        } catch (RuntimeException e) {
            registrar(descripcion, tipo.isInstance(e), "lanzó " + e.getClass().getSimpleName());
        }
    }

    private static void arbol(String entrada, char var, String esperado) {
        String descripcion = "\"" + entrada + "\" (" + var + ") -> " + esperado;
        try {
            String real = Parser.analizar(entrada, var).toString();
            registrar(descripcion, real.equals(esperado), "esperado " + esperado + " pero fue " + real);
        } catch (RuntimeException e) {
            registrar(descripcion, false, "lanzó error: " + e.getMessage());
        }
    }

    private static void valor(String entrada, char var, double x, double esperado) {
        String descripcion = "\"" + entrada + "\" en " + var + "=" + x + " -> " + esperado;
        try {
            double real = Parser.analizar(entrada.split("  ")[0], var).evaluar(x);
            registrar(descripcion, Math.abs(real - esperado) < 1e-9,
                    "esperado " + esperado + " pero fue " + real);
        } catch (RuntimeException e) {
            registrar(descripcion, false, "lanzó error: " + e.getMessage());
        }
    }

    private static void errorEvaluacion(String entrada, char var, double x) {
        String descripcion = "\"" + entrada + "\" en " + var + "=" + x + " debe dar error matemático";
        try {
            double real = Parser.analizar(entrada, var).evaluar(x);
            registrar(descripcion, false, "no lanzó error, devolvió " + real);
        } catch (ErrorMatematicoException e) {
            registrar(descripcion, true, "");
            System.out.println("          mensaje: " + e.getMessage());
        } catch (RuntimeException e) {
            registrar(descripcion, false, "lanzó otro tipo de error: " + e.getClass().getSimpleName());
        }
    }

    private static void errorSintaxis(String entrada, char var) {
        String descripcion = "\"" + entrada + "\" (" + var + ") debe dar error de sintaxis";
        try {
            Nodo_ignorar(Parser.analizar(entrada, var));
            registrar(descripcion, false, "no lanzó error");
        } catch (ExpresionInvalidaException e) {
            registrar(descripcion, true, "");
            System.out.println("          mensaje: " + e.getMessage());
        } catch (RuntimeException e) {
            registrar(descripcion, false, "lanzó otro tipo de error: " + e.getClass().getSimpleName());
        }
    }

    private static void Nodo_ignorar(Object ignorado) {
        // Solo sirve para usar el resultado del parser y que el compilador no proteste.
    }
}
