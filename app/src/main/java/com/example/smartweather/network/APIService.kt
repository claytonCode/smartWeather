package com.example.smartweather.network

import com.example.smartweather.constants.Constants.Companion.openWeatherMapApiKey
import com.example.smartweather.model.forecast.ForecastResult
import com.example.smartweather.model.weather.WeatherResult
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat : Double = 0.0,
        @Query("lon") long : Double = 0.0,
        @Query("units") units : String = "metric",
        @Query("appid") appId : String = openWeatherMapApiKey

    ):WeatherResult


    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") lat : Double = 0.0,
        @Query("lon") long : Double = 0.0,
        @Query("units") units : String = "metric",
        @Query("appid") appId : String = openWeatherMapApiKey

    ):ForecastResult
}