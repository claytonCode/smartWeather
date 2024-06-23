package com.example.smartweather.viewmodel

import android.net.wifi.rtt.CivicLocationKeys.STATE
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartweather.model.LatLong
import com.example.smartweather.model.forecast.ForecastResult
import com.example.smartweather.model.weather.WeatherResult
import com.example.smartweather.network.RetrofitClient
import kotlinx.coroutines.launch

enum class State{
    LOADING,
    SUCCESS,
    FAIL
}

class MainViewModel : ViewModel() {
    //controls the state of the viewModel
    var state by mutableStateOf(State.LOADING)

    //holds value from the weatherAPI info
    var weatherResponse : WeatherResult by mutableStateOf(WeatherResult())

    //holds value from the weatherAPI info
    var forecastResponse : ForecastResult by mutableStateOf(ForecastResult())

    //error
    var errorMessage : String by mutableStateOf("")

    fun getWeatherByLocation(latLong : LatLong ){

        viewModelScope.launch {
            state = State.LOADING
            val apiService = RetrofitClient.getInstance()
               try{
                   val apiResponse = apiService.getWeather(latLong.lat, latLong.lon)
                       weatherResponse = apiResponse
                       state = State.SUCCESS

               }catch (e : Exception){
                   errorMessage = e.message!!.toString()
                   state = State.FAIL
               }
        }
    }

    fun getForecastByLocation(latLong : LatLong ){
        viewModelScope.launch {
            state = State.LOADING
            val apiService = RetrofitClient.getInstance()
            try{
                val apiResponse = apiService.getForecast(latLong.lat, latLong.lon)
                forecastResponse = apiResponse
                state = State.SUCCESS

            }catch (e : Exception){
                errorMessage = e.message!!.toString()
                state = State.FAIL
            }
        }
    }

}