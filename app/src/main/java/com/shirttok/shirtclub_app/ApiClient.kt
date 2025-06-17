package com.shirttok.shirtclub_app

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val gson = GsonBuilder()
        .registerTypeAdapter(MessageResult::class.java, MessageResultDeserializer())
        .create()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://shirtclub.net/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}