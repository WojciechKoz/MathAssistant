package com.example.mathassistant

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MenuOwnerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgQuiz.setOnClickListener { openQuiz() }
        imgCalculator.setOnClickListener { openCalculator() }
        imgMeasure.setOnClickListener { openMeasuringSystem() }
    }

    private fun openQuiz() {
        val quizIntent = Intent(this, QuizActivity::class.java)
        startActivity(quizIntent)
    }

    private fun openCalculator() {
        val calcIntent = Intent(this, CalculatorActivity::class.java)
        startActivity(calcIntent)
    }

    private fun openMeasuringSystem() {
        val measureIntent = Intent(this, MeasureHeightActivity::class.java)
        startActivity(measureIntent)
    }
}