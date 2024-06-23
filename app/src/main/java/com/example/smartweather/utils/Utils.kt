package com.example.smartweather.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.SimpleTimeZone

class Utils {
    companion object{
        @SuppressLint("SimpleDateFormat")
        fun timeStampToHumanDate(timeStamp : Long, format: String): String{
            val sdf = SimpleDateFormat(format)
            return sdf.format(timeStamp * 1000)

        }
        fun buildIcon(icon : String, isBigSize : Boolean = true) : String{
                return if(isBigSize){
                    "https://openweathermap.org/img/wn/$icon@4x.png"
                }else{
                    "https://openweathermap.org/img/wn/$icon.png"
                }
        }

    }

}