package com.example.mathassistant

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class Question(
        val text: String = "",
        val answers: ArrayList<String> = ArrayList()
): Serializable {
    companion object {
        fun loadQuestions(filename: String, context: Context): ArrayList<Question> {
            val output = ArrayList<Question>()

            try {
                val inputStream = context.assets.open(filename)
                val buffer = ByteArray(inputStream.available())
                inputStream.read(buffer)
                inputStream.close()

                val questions = JSONObject(String(buffer, Charsets.UTF_8)).getJSONArray("questions")

                for(i in 0 until questions.length()) {
                    val jsonAnswers = questions.getJSONObject(i).getJSONArray("answers")
                    val answers = ArrayList<String>()

                    for(j in 0 until jsonAnswers.length()) {
                        answers.add(jsonAnswers.getString(j))
                    }
                    Log.d("DBG", "ans: $answers")

                    output.add(Question(questions.getJSONObject(i).getString("question"), answers))
                }
            } catch(e: JSONException) {
                e.printStackTrace()
            }

            return output
        }
    }

}