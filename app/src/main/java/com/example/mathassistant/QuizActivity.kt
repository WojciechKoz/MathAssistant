package com.example.mathassistant

import android.content.Intent
import kotlinx.android.synthetic.main.activity_quiz.*
import android.os.Bundle
import android.widget.RadioButton
import kotlin.random.Random

class QuizActivity : MenuOwnerActivity() {
    // keeps list of questions as a static field to avoid loading them from file many times
    companion object {
        var questions: ArrayList<Question> = ArrayList()
    }

    private var questionIndex = 0
    private lateinit var selectedQuestion: Question
    private var rightAnswer: String = ""
    private var submited = false
    private var radioButtons: ArrayList<RadioButton> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        btnNext.setOnClickListener { showResults() }

        if(questions.size == 0) { // evaluates only once
            questions = Question.loadQuestions("questions.json", this)
        }

        // references to all check buttons to be able to work with them using the loop
        radioButtons.add(rbtnAns1)
        radioButtons.add(rbtnAns2)
        radioButtons.add(rbtnAns3)
        radioButtons.add(rbtnAns4)
        radioButtons.add(rbtnAns5)

        // initializes the problem when the activity is opened otherwise copies from the saved instance state
        // because the on create method is launched the user is not going back from the solution page and
        // we don't have to worry about the previous question so put -1 as an impossible index
        initProblem(savedInstanceState, -1)
    }


    override fun onResume() {
        /**
         * function that creates new question when the activity is resumed.
         * (user goes back from the solution page)
         * therefore the question has to be different that the previous one
         */
        super.onResume()
        if(submited) {
            initProblem(null, questionIndex)
            submited = false
        }
    }

    private fun initProblem(savedInstanceState: Bundle?, previousIndex: Int) {
        // by default, checks the first answer
        rbtnAns1.isChecked = true

        // if saved instance state exists and contains the question index then loads this question
        // otherwise choose the question at random but different the the previous one
        if(savedInstanceState?.getInt("index") != null) {
            questionIndex = savedInstanceState.getInt("index")
        } else {
            do { questionIndex = Random.nextInt(questions.size) } while(questionIndex == previousIndex)
        }

        // selects question with the selected question index and
        // initializes answers and the right answer index according to this question
        selectedQuestion = questions[questionIndex]
        rightAnswer = selectedQuestion.answers[0]
        lblQuestion.text = selectedQuestion.text

        if(savedInstanceState != null) {
            for(i in 0 until 5) {
                radioButtons[i].text = savedInstanceState.getString("ans" + (i+1))
            }
        } else {
            initAnswers()
        }
    }

    private fun initAnswers() {
        // fills radio buttons with shuffled list of answers
        val indexes = (0..4).shuffled(Random)
        for(i in 0 until 5) {
            radioButtons[i].text = selectedQuestion.answers[indexes[i]]
        }
    }

    override fun onSaveInstanceState(activityInstanceState: Bundle) {
        /**
         * when the screen is rotated or the dark mode is changed saves question and answers
          */
        super.onSaveInstanceState(activityInstanceState)
        activityInstanceState.putInt("index", questionIndex)
        for(i in 0 until 5) {
            activityInstanceState.putString("ans"+(i+1), radioButtons[i].text.toString())
        }
    }

    private fun showResults() {
        submited = true
        val id = rgrpAnswers.checkedRadioButtonId
        val selectedAns = findViewById<RadioButton>(id).text.toString()

        val resultsIntent = Intent(this, QuizResultsActivity::class.java).apply {
            putExtra("question", selectedQuestion.text)
            for(i in 0 until 5) {
                putExtra("ans"+(i+1), radioButtons[i].text.toString())
            }
            putExtra("rightAns", selectedQuestion.answers[0])
            putExtra("selectedAns", selectedAns)
            putExtra("index", questionIndex) // for showing the solution
        }
        startActivity(resultsIntent)
    }
}