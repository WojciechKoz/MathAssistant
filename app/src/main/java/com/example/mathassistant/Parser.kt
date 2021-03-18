package com.example.mathassistant

import java.lang.Double.NaN
import kotlin.math.*

/**
 * object to :
 * - transform strings to kotlin lambda
 * - transform input from calculator to link for API
 *
 * An example of how it works (visualize as a tree)
 * input = "sin(x) + 3*x"
 *                           "+"
 *                         /    \
 *                     "sin"    "*"
 *                       |     /   \
 *                      "x"   "3"  "x"
 *  "x"    - {x -> x}
 *  "3"    - {_ -> 3.0}
 *  "sin"  - {x -> sin(x)}
 *  "*"    - {x -> left(x) * right(x)}
 *  "+"    - {x -> left(x) + right(x)}
 *  These lambdas are nested to each other using recursion
 *  NOTE: functions names are shortened to one letter e.g. sin(x) - s(x) to make this algorithm easier
 */
object Parser {
        private fun isFunction(func: String): Boolean {
            return listOf("s(", "c(", "t(", "r(", "a(", "l(", "e(", "S(", "C(", "T(").contains(func)
        }

        private fun isOperator(char: Char): Boolean {
            return listOf('+', '-', '*', '/', '^').contains(char)
        }

        private fun joinRange(items: ArrayList<String>, begin: Int, end: Int): String {
            val output = StringBuilder()
            for(i in begin until end) {
                output.append(items[i])
            }
            return output.toString()
        }

        private fun split(text: String): ArrayList<String> {
            val output = ArrayList<String>()
            var bracketControl = 0
            var functionNameSkip = false
            var term = ""

            for (char in text) {

                if(functionNameSkip) {
                    term += char
                    functionNameSkip = false
                    continue
                }

                if(bracketControl < 0) {
                    term += char
                    if(char == '(') {
                        bracketControl -= 1
                    } else if(char == ')') {
                        bracketControl += 1
                    }

                    if(bracketControl == 0) {
                        output.add(term)
                        term = ""
                    }
                } else if(char == '(' || isFunction("$char(")) {
                    functionNameSkip = char != '('
                    bracketControl -= 1
                    if(term != "") {
                        output.add(term)
                        term = ""
                    }
                    term += char
                } else if(isOperator(char)) {
                    if(term != "") {
                        output.add(term)
                        term = ""
                    }
                    output.add(char.toString())
                } else {
                    term += char
                }
            }
            if(term != "") {
                output.add(term)
            }
            return output
        }

        private fun indexOfMainOperation(items: ArrayList<String>): Int {
            var addSubIndexes = -1
            var mulDivIndexes = -1
            var powerIndexes = -1

            for (i in 0 until items.size) {
                val item = items[i]
                if(item == "+" || item == "-") {
                    addSubIndexes = i
                } else if(item == "*" || item == "/") {
                    mulDivIndexes = i
                } else if(item == "^" && powerIndexes == -1) {
                    powerIndexes = i
                }
            }

            if(addSubIndexes != -1) {
                 return addSubIndexes
            } else if(mulDivIndexes != -1) {
                return mulDivIndexes
            }
            return powerIndexes
        }

        private fun evaluate(formula: String): (Double) -> Double {
            val items = split(formula)

            if(items.size == 0) {
                return { _ -> 0.0 }
            }

            if(items.size == 1) {
                val expression = items[0]

                if(expression == "x") {
                    return {x -> x}
                }

                if(expression[0] == '(') {
                    return evaluate(expression.substring(1, expression.length - 1))
                }
                if(expression.length >= 3) {
                    val function = expression.substring(0, 2)
                    if (isFunction(function)) {
                        val inside = evaluate(expression.substring(2, expression.length - 1))

                        when (function) {
                            "c(" -> {
                                return { x -> cos(inside(x)) }
                            }
                            "s(" -> {
                                return { x -> sin(inside(x)) }
                            }
                            "t(" -> {
                                return { x -> tan(inside(x)) }
                            }
                            "C(" -> {
                                return {x -> acos(inside(x))}
                            }
                            "S(" -> {
                                return {x -> asin(inside(x))}
                            }
                            "T(" -> {
                                return {x -> atan(inside(x))}
                            }
                            "a(" -> {
                                return { x -> abs(inside(x)) }
                            }
                            "r(" -> {
                                return { x -> sqrt(inside(x)) }
                            }
                            "l(" -> {
                                return { x -> ln(inside(x)) }
                            }
                            "e(" -> {
                                return { x -> exp(inside(x)) }
                            }
                        }
                    }
                }
                var output = expression.toDoubleOrNull()
                if(output == null) output = NaN
                return {_ -> output}
            }
            val index = indexOfMainOperation(items)

            if(index != -1) {
                val operation = items[index]
                val left = evaluate(joinRange(items, 0, index))
                val right = evaluate(joinRange(items, index+1, items.size))

                when(operation) {
                    "+" -> return {x -> left(x) + right(x)}
                    "-" -> return {x -> left(x) - right(x)}
                    "*" -> return {x -> left(x) * right(x)}
                    "/" -> return {x -> left(x) / right(x)}
                    "^" -> return {x -> left(x).pow(right(x))}
                }
            }
            return {_ -> NaN}
        }

        private fun cleanFormula(formula: String): String {
            var output = formula
            var i = 1
            while(i < output.length-1) {
                if(output[i] == ' ' && !isOperator(output[i-1]) && !isOperator(output[i+1])) {
                    output = output.substring(0, i)+"*"+output.substring(i)
                    i -= 1
                }
                i += 1
            }

            output = output.replace(" ", "")
            i = 1
            while(i < output.length) {
                if(listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(output[i-1]) &&
                         listOf('x', 'c', 's', 't', 'C', 'S', 'T', 'l', '(', 'e', 'a').contains(output[i])) {
                    output = output.substring(0, i)+"*"+output.substring(i)
                    i -= 1
                }
                i += 1
            }
            output = output
                    .replace("asin", "S")
                    .replace("arcsin", "S")
                    .replace("acos", "C")
                    .replace("arccos", "C")
                    .replace("atan", "T")
                    .replace("arctan", "T")
                    .replace("cos", "c")
                    .replace("sin", "s")
                    .replace("tan", "t")
                    .replace("abs", "a")
                    .replace("sqrt", "r")
                    .replace("exp", "e")
                    .replace("pi", "3.1415")
                    .replace("log", "l")
                    .replace("÷", "/")
                    .replace("×", "*")
                    .replace("√", "r")
            return output
        }

        fun parseStringToFunction(formula: String): (Double) -> Double {
            val cleared = cleanFormula(formula)
            return evaluate(cleared)
        }

        fun parseInputToLink(input: String): String {
            return input.replace("×", "*")
                    .replace("√", "sqrt")
                    .replace("+", "%2B")
                    .replace("/", "%2F")
                    .replace("^", "%5E")
                    .replace("÷", "%2F")
                    .replace("atan", "arctan")
                    .replace("asin", "arcsin")
                    .replace("acos", "arccos")
        }
    }