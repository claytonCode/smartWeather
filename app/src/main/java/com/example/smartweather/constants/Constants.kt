package com.example.smartweather.constants

class Constants {

    companion object{
            val permissions = arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
            )

           const val openWeatherMapApiKey = "d2d961dfb1d31ce90377536df63849f2"

           const val  colorBg1 = 0xff08203e
           const val  colorBg2 =  0xff557c93
           const val  cardColor = 0xFF333639

           const val LOADING = "loaning..."

           const val NA = "N/A"
    }

}