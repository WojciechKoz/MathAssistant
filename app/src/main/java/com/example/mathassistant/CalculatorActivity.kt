package com.example.mathassistant

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_calculator.*


class CalculatorActivity : MenuOwnerActivity() {
    var buttons: List<Button> = ArrayList()

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        txtInput.showSoftInputOnFocus = false
        txtInput.requestFocus()

        buttons = listOf(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnSin, btnCos,
        btnTan, btnExp, btnLog, btnAsin, btnAcos, btnAtan, btnAbs, btnMul, btnLeftBracket, btnRightBracket,
        btnVar, btnAdd, btnSub, btnDiv, btnSqrt, btnDot, btnPow)

        for(button in buttons) {
            button.setOnClickListener {
                var additionalBracket = ""
                val cursorPosition = txtInput.selectionStart
                if(listOf("sin", "cos", "tan", "asin", "acos", "atan", "exp", "log", "abs", "√").contains(button.text.toString()) ) {
                    additionalBracket = "("
                }
                val text = button.text.toString() + additionalBracket
                txtInput.setText(txtInput.text.substring(0, cursorPosition) + text + txtInput.text.substring(cursorPosition))
                txtInput.setSelection(cursorPosition + text.length)
            }
        }

        btnBackspace.setOnClickListener {
            // deletes the character from formula depending where the cursor is
            // when we have got "cos(" it deletes the function name with the bracket
            if(txtInput.selectionStart != 0) {
                val text = txtInput.text.toString()
                val cursorPosition = txtInput.selectionStart
                var done = false
                if(text[cursorPosition-1] == '(') {

                    // checks if there is four letters function before the cursor to delete the name at once
                    if(cursorPosition >= 5) {
                        val chunk = text.substring(cursorPosition-5, cursorPosition-1)
                        if(listOf("atan", "acos", "asin").contains(chunk)) {
                            txtInput.setText(text.substring(0, cursorPosition-5)+txtInput.text.substring(cursorPosition))
                            txtInput.setSelection(cursorPosition-5)
                            done = true
                        }
                    }
                    // checks if there is three letters function before the cursor to delete the name at once
                    if(cursorPosition >= 4 && !done) {
                        val chunk = text.substring(cursorPosition-4, cursorPosition-1)
                        if(listOf("tan", "cos", "sin", "exp", "log", "abs").contains(chunk)) {
                            txtInput.setText(text.substring(0, cursorPosition-4)+txtInput.text.substring(cursorPosition))
                            txtInput.setSelection(cursorPosition-4)
                            done = true
                        }
                    }
                    // checks if there is one letter function ( square root)
                    // before the cursor to delete the name at once
                    if(cursorPosition >= 2 && !done) {
                        val chunk = text.substring(cursorPosition-2, cursorPosition-1)
                        if(chunk == btnSqrt.text) {
                            txtInput.setText(text.substring(0, cursorPosition-2)+txtInput.text.substring(cursorPosition))
                            txtInput.setSelection(cursorPosition-2)
                            done = true
                        }
                    }
                }

                // if it was not a function name to be deleted deletes only one char
                if(!done) {
                    txtInput.setText(txtInput.text.substring(0, cursorPosition-1) + txtInput.text.substring(cursorPosition))
                    txtInput.setSelection(cursorPosition-1)
                }
            }
        }

        btnSubmit.setOnClickListener {
            val input = txtInput.text.toString()

            val resultsIntent = Intent(this, ResultsActivity::class.java).apply {
                putExtra("formula", input)
            }
            startActivity(resultsIntent)
        }

    }

}