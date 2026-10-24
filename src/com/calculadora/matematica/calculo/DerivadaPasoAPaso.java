package com.calculadora.matematica.calculo;

import com.calculadora.matematica.expresion.Constante;
import com.calculadora.matematica.expresion.Funcion;
import com.calculadora.matematica.expresion.Nodo;
import com.calculadora.matematica.expresion.Numero;
import com.calculadora.matematica.expresion.Operacion;
import com.calculadora.matematica.expresion.Variable;
import java.util.ArrayList;
import java.util.List;

/** Construye una explicación legible de las reglas usadas al derivar un árbol. */
public final class DerivadaPasoAPaso {

    private DerivadaPasoAPaso() {
    }

    public static String generar(Nodo expresion, char variable) {
        List<String> pasos = new ArrayList<>();
        derivar(expresion, variable, pasos);
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < pasos.size(); i++) {
            if (i > 0) {
                texto.append("\n\n");
            }
            texto.append("PASO ").append(i + 1).append(" · ").append(pasos.get(i));
        }
        return texto.toString();
    }

    private static Nodo derivar(Nodo n, char variable, List<String> pasos) {
        if (n instanceof Numero || n instanceof Constante) {
            pasos.add("Constante: d/d" + variable + "(" + n + ") = 0.");
            return Calculadora.derivar(n, variable);
        }
        if (n instanceof Variable) {
            Variable v = (Variable) n;
            String resultado = v.getNombre().equalsIgnoreCase(String.valueOf(variable)) ? "1" : "0";
            pasos.add("Variable: d/d" + variable + "(" + n + ") = " + resultado + ".");
            return Calculadora.derivar(n, variable);
        }
        if (n instanceof Operacion) {
            Operacion op = (Operacion) n;
            Nodo izquierda = op.getIzq();
            Nodo derecha = op.getDer();
            Nodo di = derivar(izquierda, variable, pasos);
            Nodo dd = derivar(derecha, variable, pasos);
            String regla;
            switch (op.getOp()) {
                case '+':
                    regla = "Suma: se deriva cada término. d/d" + variable + "(" + n + ") = " + di + " + " + dd + ".";
                    break;
                case '-':
                    regla = "Resta: se deriva cada término. d/d" + variable + "(" + n + ") = " + di + " - " + dd + ".";
                    break;
                case '*':
                    regla = "Producto: (u·v)' = u'·v + u·v'. d/d" + variable + "(" + n + ") = (" + di + ")·(" + derecha + ") + (" + izquierda + ")·(" + dd + ").";
                    break;
                case '/':
                    regla = "Cociente: (u/v)' = (u'v - uv')/v². d/d" + variable + "(" + n + ") = ((" + di + ")·(" + derecha + ") - (" + izquierda + ")·(" + dd + "))/(" + derecha + ")².";
                    break;
                case '^':
                    if (derecha instanceof Numero && esConstanteRespecto(dd)) {
                        regla = "Potencia: (uⁿ)' = n·u⁽ⁿ⁻¹⁾·u'. d/d" + variable + "(" + n + ") = (" + derecha + ")·(" + izquierda + ")^(" + derecha + " - 1)·(" + di + ").";
                    } else {
                        regla = "Potencia con base y exponente variables: (uᵛ)' = uᵛ·(v'·ln(u) + v·u'/u). "
                                + "En esta expresión: d/d" + variable + "(" + n + ") = (" + n + ")·((" + dd
                                + ")·ln(" + izquierda + ") + (" + derecha + ")·(" + di + ")/" + izquierda + ").";
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Operador no soportado: " + op.getOp());
            }
            pasos.add(regla);
            return Calculadora.derivar(n, variable);
        }
        if (n instanceof Funcion) {
            Funcion funcion = (Funcion) n;
            Nodo argumento = funcion.getArgumento();
            Nodo derivadaArgumento = derivar(argumento, variable, pasos);
            String formula = reglaFuncion(funcion.getNombre(), argumento, derivadaArgumento, variable);
            pasos.add("Regla de la cadena: se deriva la función externa y se multiplica por la derivada interna. " + formula);
            return Calculadora.derivar(n, variable);
        }
        throw new IllegalArgumentException("Tipo de expresión no reconocido: " + n.getClass().getSimpleName());
    }

    private static boolean esConstanteRespecto(Nodo derivada) {
        return derivada instanceof Numero && ((Numero) derivada).getValor().esCero();
    }

    private static String reglaFuncion(String nombre, Nodo u, Nodo du, char variable) {
        String interna = "(" + du + ")";
        switch (nombre) {
            case "sin": return "d/d" + variable + "[sin(" + u + ")] = cos(" + u + ")·" + interna + ".";
            case "cos": return "d/d" + variable + "[cos(" + u + ")] = -sin(" + u + ")·" + interna + ".";
            case "tan": return "d/d" + variable + "[tan(" + u + ")] = sec(" + u + ")²·" + interna + ".";
            case "cot": return "d/d" + variable + "[cot(" + u + ")] = -csc(" + u + ")²·" + interna + ".";
            case "sec": return "d/d" + variable + "[sec(" + u + ")] = sec(" + u + ")·tan(" + u + ")·" + interna + ".";
            case "csc": return "d/d" + variable + "[csc(" + u + ")] = -csc(" + u + ")·cot(" + u + ")·" + interna + ".";
            case "ln": return "d/d" + variable + "[ln(" + u + ")] = " + interna + "/(" + u + ").";
            case "log": return "d/d" + variable + "[log(" + u + ")] = " + interna + "/((" + u + ")·ln(10)).";
            case "exp": return "d/d" + variable + "[exp(" + u + ")] = exp(" + u + ")·" + interna + ".";
            case "sqrt": return "d/d" + variable + "[√(" + u + ")] = " + interna + "/(2·√(" + u + ")).";
            case "asin": return "d/d" + variable + "[asin(" + u + ")] = " + interna + "/√(1 - (" + u + ")²).";
            case "acos": return "d/d" + variable + "[acos(" + u + ")] = -" + interna + "/√(1 - (" + u + ")²).";
            case "atan": return "d/d" + variable + "[atan(" + u + ")] = " + interna + "/(1 + (" + u + ")²).";
            default: throw new IllegalArgumentException("Regla de derivación no disponible para " + nombre + ".");
        }
    }
}
