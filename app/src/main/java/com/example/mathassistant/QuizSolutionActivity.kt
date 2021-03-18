package com.example.mathassistant

import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_quiz_solution.*

class QuizSolutionActivity : MenuOwnerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_solution)
        val index = intent.getIntExtra("index", 0)
        val dark = if(DarkModeManager.isLight()) "" else "dark"
        showPdfFromAssets("solution$index$dark.pdf")
    }

    private fun showPdfFromAssets(pdfName: String) {
        pdfView.fromAsset(pdfName)
                .password(null) // if password protected, then write password
                .defaultPage(0) // set the default page to open
                .onPageError { page, _ ->
                    Toast.makeText( this, "Error at page: $page", Toast.LENGTH_LONG ).show()
                }
                .load()
    }
}