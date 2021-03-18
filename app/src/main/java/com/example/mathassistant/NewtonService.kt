package com.example.mathassistant

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NewtonService {
    @GET("{operation}/{formula}")
    fun getSolution(@Path("operation") operation: String, @Path(value = "formula", encoded = true) formula: String): Call<NewtonSolution>
}