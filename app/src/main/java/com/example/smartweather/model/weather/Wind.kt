package com.example.smartweather.model.weather

import com.google.gson.annotations.SerializedName

data class Wind(

    @SerializedName("speed") var speed :Double? = null,
    @SerializedName("deg") var deg : Double? = null
)
