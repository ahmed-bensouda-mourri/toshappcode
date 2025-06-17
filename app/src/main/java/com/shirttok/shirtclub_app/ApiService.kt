package com.shirttok.shirtclub_app

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("message/{shirtNumber}") // This is the endpoint for retrieving a message by shirt number
    fun getMessage(@Path("shirtNumber") shirtNumber: String): Call<ApiResponse> // Call with dynamic shirt number
}