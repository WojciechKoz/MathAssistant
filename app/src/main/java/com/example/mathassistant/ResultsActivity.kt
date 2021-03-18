package com.example.mathassistant

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_results.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultsActivity : MenuOwnerActivity() {
    private val functions: ArrayList<MathFunction> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        if(savedInstanceState?.getString("input") != null &&
                savedInstanceState.getString("derive") != null &&
                savedInstanceState.getString("integral") != null) {
            functions.add(MathFunction(savedInstanceState.getString("input")!!, MathFunction.INPUT))
            functions.add(MathFunction(savedInstanceState.getString("derive")!!, MathFunction.DERIVE))
            functions.add(MathFunction(savedInstanceState.getString("integral")!!, MathFunction.INTEGRATE))
            fillLayout(functions)
        } else {
            val input = intent.getStringExtra("formula")
            val link = Parser.parseInputToLink(input!!)

            functions.add(MathFunction(input, MathFunction.INPUT))
            loadData("derive", link)
            loadData("integrate", link)
        }
    }

    private fun fillLayout(functions: ArrayList<MathFunction>) {
        content.layoutManager = LinearLayoutManager(this)
        content.adapter = FunctionAdapter(this, functions)
    }

    private fun loadData(operation: String, formula: String) {
        val service = ServiceBuilder.buildService(NewtonService::class.java)
        val requestCall = service.getSolution(operation, formula)

        requestCall.enqueue(object: Callback<NewtonSolution> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<NewtonSolution>,
                response: Response<NewtonSolution>
            ) {
                val type = if(operation == "derive") MathFunction.DERIVE else MathFunction.INTEGRATE
                if(response.isSuccessful) {
                    val solution = response.body()!!
                    functions.add(MathFunction(solution.result, type))

                } else {
                    Toast.makeText(this@ResultsActivity, "API couldn't solve the $operation", Toast.LENGTH_LONG).show()
                    functions.add(MathFunction("No data", type))
                }
                if(functions.size == 3) {
                    fillLayout(ArrayList(functions.sortedWith(compareBy { it.type })))
                }
            }

            override fun onFailure(call: Call<NewtonSolution>, t: Throwable) {
                Toast.makeText(this@ResultsActivity, "No response from the API. Check your internet", Toast.LENGTH_LONG).show()

                val type = if(operation == "derive") MathFunction.DERIVE else MathFunction.INTEGRATE
                functions.add(MathFunction("No data", type))

                if(functions.size == 3) {
                    fillLayout(ArrayList(functions.sortedWith(compareBy { it.type })))
                }
            }
        })
    }

    override fun onSaveInstanceState(activityInstanceState: Bundle) {
        super.onSaveInstanceState(activityInstanceState)

        if(functions.size == 3) {
            activityInstanceState.putString("input", functions[0].expression)
            activityInstanceState.putString("derive", functions[1].expression)
            activityInstanceState.putString("integral", functions[2].expression)
        }
    }
}