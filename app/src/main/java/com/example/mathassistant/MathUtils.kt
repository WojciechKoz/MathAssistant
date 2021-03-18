package com.example.mathassistant

import java.lang.Double.NaN
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {
    fun mean(data: ArrayList<Double>): Double {
        if(data.size == 0) return NaN

        var sum = 0.0
        for(i in 0 until data.size) {
            sum += data[i]
        }
        return sum / data.size
    }

    fun stdDev(data: ArrayList<Double>): Double {
        if(data.size == 0) return NaN

        val mean = mean(data)
        var sum = 0.0
        for(i in 0 until data.size) {
            sum += (data[i] - mean).pow(2)
        }
        return sqrt(sum / data.size)
    }

    fun functionMightNotBeContinuousAt(prevY: Double, y: Double): Boolean {
        /**
         * looks for big differences between two following y values and decides if the function
         * is continuous if this point or not
         * : prevY - first y value
         * : y - y value followed by prevY
         * : return - true if the function is probably not continues otherwise false
         */
        return abs(y) > 10 * abs(prevY) && prevY > 0.1 || abs(prevY-y) > 15
    }

    fun round(number: Double, decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (number * multiplier).toInt() / multiplier
    }

    fun degreesToRadians(angle: Double): Double {
        return PI*angle/180.0
    }

    fun radiansToDegrees(angle: Double): Double {
        return 180.0*angle/PI
    }
}