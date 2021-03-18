package com.example.mathassistant

import android.content.Intent
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import kotlinx.android.synthetic.main.activity_quiz_results.*


class QuizResultsActivity : MenuOwnerActivity() {
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_results)

        lblQuestion.text = intent.getStringExtra("question")
        index = intent.getIntExtra("index", 0)
        val answers = listOf(lblAns1, lblAns2, lblAns3, lblAns4, lblAns5)

        for(i in 1 until 6) {
            answers[i-1].text = intent.getStringExtra("ans$i")

            if(answers[i-1].text == intent.getStringExtra("rightAns")) {
                answers[i-1].setTextColor(GREEN)
            } else {
                answers[i-1].setTextColor(RED)
            }

            if(answers[i-1].text == intent.getStringExtra("selectedAns")) {
                val content = SpannableString(answers[i-1].text)
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                answers[i-1].text = content
            }
        }

        btnNext.setOnClickListener {
            finish()
        }

        btnCalculations.setOnClickListener {
            val solutionIntent = Intent(this, QuizSolutionActivity::class.java).apply {
                putExtra("index", index)
            }
            startActivity(solutionIntent)
        }
    }
}