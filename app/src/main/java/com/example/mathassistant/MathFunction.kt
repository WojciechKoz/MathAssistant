package com.example.mathassistant

import java.lang.Double.NaN

class MathFunction(var expression: String, val type: Int) {
    // type <- {INPUT, DERIVE, INTEGRATE}.
    // The arraylist of mathfunction will be sorted with respect to this value
    // constants for keeping the right order in recyclerView
    companion object {
        const val INPUT = 0
        const val DERIVE = 1
        const val INTEGRATE = 2
    }

    private val eval = if(expression != "No data") Parser.parseStringToFunction(expression)
                                       else { _ ->NaN}

    fun getHeader(): String {
        return when(type) {
            INPUT -> "Input function"
            DERIVE -> "Derivative function"
            INTEGRATE -> "Antiderivative function"
            else -> "Function"
        }
    }

    fun getFormula(): String {
        val prefix = when(type) {
            INPUT -> "f(x) = "
            DERIVE -> "f'(x) = "
            INTEGRATE -> "F(x) = "
            else -> "f(x) = "
        }
        return prefix + (if(expression != "No data") expression else "impossible to calculate")
    }

    fun evaluate(x: Double): Double {
        return eval(x)
    }
}