package com.example.smartweather.model.forecast

import com.google.gson.annotations.SerializedName

data class ForecastResult(
    @SerializedName("cod") var cod: Double? = null,
    @SerializedName("message") var message : String? = null,
    @SerializedName("cnt") var cnt: Int? = null,
    @SerializedName("list") var list : ArrayList<CustomList>? = arrayListOf(),
    @SerializedName("city") var city: City? = City()


)
