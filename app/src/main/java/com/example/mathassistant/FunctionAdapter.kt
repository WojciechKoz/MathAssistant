package com.example.mathassistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.function_layout.view.*

class FunctionAdapter(private val context: ResultsActivity,
                      private val functions: ArrayList<MathFunction>):
        RecyclerView.Adapter<FunctionAdapter.ViewHolder>() {
    companion object {
        private const val DOMAIN_MIN_VALUE = -6.0
        private const val DOMAIN_MAX_VALUE = 6.0
        private const val DOMAIN_STEP = 0.05
        private const val MAX_DATA_POINTS = 400
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.function_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return functions.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val function = functions[position]
        holder.header.text = function.getHeader()
        holder.formula.text = function.getFormula()
        fillGraph(function, holder)
    }

    private fun fillGraph(function: MathFunction, holder: ViewHolder) {
        var series = LineGraphSeries<DataPoint>()

        var x = DOMAIN_MIN_VALUE
        val valuesOfFunction = ArrayList<Double>()

        // for checking for asymptotes
        var prevFunctionValue = 0.0

        // for setting x range on the plot
        var minX = DOMAIN_MAX_VALUE
        var maxX = DOMAIN_MIN_VALUE

        while(x < DOMAIN_MAX_VALUE) {
            val y = function.evaluate(x)
            if(y.isNaN() || y.isInfinite() || MathUtils.functionMightNotBeContinuousAt(prevFunctionValue, y)) {
                if(!series.isEmpty) {
                    holder.plot.addSeries(series)
                    series = LineGraphSeries()
                }
            } else {
                series.appendData(DataPoint(x, y), false, MAX_DATA_POINTS)
                valuesOfFunction.add(y)
                if(x < minX) {
                    minX = x
                } else if(x > maxX) {
                    maxX = x
                }
            }
            prevFunctionValue = y

            x += DOMAIN_STEP
        }

        holder.plot.addSeries(series)

        // if minX is less than maxX means that there is at least one point in the dataset
        // and we can set x and y ranges on the plot
        if(maxX - minX > DOMAIN_STEP/2) {
            holder.plot.viewport.setMinX(minX)
            holder.plot.viewport.setMaxX(maxX)
            holder.plot.viewport.isXAxisBoundsManual = true

            // calculates standard deviation of points, if the function is constant
            // and all values are the same (dev is small) then dev = 1 to avoid an error
            var dev = MathUtils.stdDev(valuesOfFunction)
            dev = if(dev < 0.1) 1.0 else dev

            val mean = MathUtils.mean(valuesOfFunction)

            // sets y range on the plot as (mean-3*dev, mean+3*dev)
            // means that the average of y values will be on the middle of the screen and the range
            // will be related to standard deviation of values (3 was chosen because it fits quite well)
            holder.plot.viewport.setMinY(mean - 3*dev)
            holder.plot.viewport.setMaxY(mean + 3*dev)
            holder.plot.viewport.isYAxisBoundsManual = true
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val header: TextView = view.lblHeader
        val formula: TextView = view.lblFormula
        val plot: GraphView = view.plot
    }

}