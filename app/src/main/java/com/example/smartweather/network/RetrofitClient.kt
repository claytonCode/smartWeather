package com.example.smartweather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitClient {

    companion object {

        private var apiService: APIService? = null

        fun getInstance(): APIService{
            if (apiService == null){
                apiService = Retrofit.Builder()
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(APIService::class.java)
            }
                return apiService!!
        }

    }
}