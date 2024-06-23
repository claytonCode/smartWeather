   package com.example.smartweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.smartweather.constants.Constants.Companion.colorBg1
import com.example.smartweather.constants.Constants.Companion.colorBg2
import com.example.smartweather.constants.Constants.Companion.permissions
import com.example.smartweather.model.LatLong
import com.example.smartweather.model.forecast.ForecastResult
import com.example.smartweather.model.weather.WeatherResult
import com.example.smartweather.ui.theme.SmartWeatherTheme
import com.example.smartweather.view.ForecastSection
import com.example.smartweather.view.WeatherSection
import com.example.smartweather.viewmodel.MainViewModel
import com.example.smartweather.viewmodel.State
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.coroutineScope

   class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
       private lateinit var mainViewModel: MainViewModel
    private var locationRequired : Boolean = false


       override fun onResume() {
           super.onResume()
           if(locationRequired) startLocationUpdate()
       }


       override fun onPause() {
           super.onPause()
           locationCallback?.let {
               fusedLocationProviderClient?.removeLocationUpdates(it)
           }

       }

       @SuppressLint("MissingPermission")
       private fun startLocationUpdate() {
           locationCallback?.let {
               val locationRequest = LocationRequest.Builder(
                   Priority.PRIORITY_HIGH_ACCURACY, 100
               )
                   .setWaitForAccurateLocation(false)
                   .setMinUpdateIntervalMillis(3000)
                   .setMinUpdateIntervalMillis(100)
                   .build()

               fusedLocationProviderClient?.requestLocationUpdates(
                   locationRequest,
                   it,
                   Looper.getMainLooper()
               )

           }
       }

       override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         initLocationClient()

         initViewModel()

        //enableEdgeToEdge()
        setContent {
            // this will keep the value of our current location
            var currentLocation by remember {
                mutableStateOf(LatLong(0.0, 0.0 ))
            }
            //implement location call back
            locationCallback = object :LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for(location in p0.locations){
                        currentLocation = LatLong(
                            location.latitude,
                            location.longitude
                        )
                    }

                }
            }

            SmartWeatherTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),



                ){
                    LocationScreen(this@MainActivity ,currentLocation)
                 }

              }
           }
       }

       private fun fetchWeatherInformation(mainViewModel: MainViewModel, currentLocation: LatLong){
           mainViewModel.state = State.LOADING
           mainViewModel.getWeatherByLocation(currentLocation)
           mainViewModel.getForecastByLocation(currentLocation)
           mainViewModel.state = State.SUCCESS
       }

       private fun initViewModel() {
           mainViewModel = ViewModelProvider(this@MainActivity)[MainViewModel::class.java]

       }

       @Composable
       private fun LocationScreen(context : Context,currentLocation: LatLong) {


           //request runtime permission
           val launchMultiplePermissions = rememberLauncherForActivityResult(
               ActivityResultContracts.RequestMultiplePermissions()

           ) {permissionMap ->
               val areGranted  = permissionMap.values.reduce {
                   accepted , next -> accepted && next
                     }
               //check if app permissions are accepted
               if(areGranted){
                   locationRequired = true
                   startLocationUpdate()
                   Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
               }
               else {
                   Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
               }
           }

           val systemUiController = rememberSystemUiController()
           DisposableEffect( key1 = true, effect ={
               systemUiController.isSystemBarsVisible = false // hides status bar
               onDispose {
                   systemUiController.isSystemBarsVisible = false //shows status bar
               }
           })

           LaunchedEffect(key1 = currentLocation, block = {
               coroutineScope {
                   if(permissions.all{
                       ContextCompat.checkSelfPermission(
                           context,
                            it

                       )==PackageManager.PERMISSION_GRANTED
                       }){
                       //if permission is accepted
                       startLocationUpdate()
                   }
                     else{
                         launchMultiplePermissions.launch(permissions)
                     }

               }
           })
           LaunchedEffect(key1 = true, block = {
               fetchWeatherInformation(mainViewModel, currentLocation)
               //loads current location to avoid auto API calling for performance dent
           })



           val gradient = Brush.linearGradient(
               colors = listOf( Color(colorBg1), Color(colorBg2)),
               start = Offset(1000f, 1000f),
               end = Offset(1000f, 1000f)

           )

         Box(
             modifier = Modifier
                 .fillMaxSize()
                 .background(gradient),
             contentAlignment = Alignment.BottomCenter
         ){
             val screenHeight = LocalConfiguration.current.screenHeightDp.dp
             val marginTop = screenHeight * 0.2f
             val marginTopPx = with(LocalDensity.current){marginTop.toPx()}

              Column(
                 modifier = Modifier
                     .verticalScroll(rememberScrollState())
                     .layout { measurable, constraints ->
                         val placeable = measurable.measure(constraints)

                         //define the child layout
                         layout(
                             placeable.width,
                             placeable.height + marginTopPx.toInt()

                         ) {
                             placeable.placeRelative(0, marginTopPx.toInt())
                         }

                     },
                 verticalArrangement = Arrangement.Center,
                 horizontalAlignment = Alignment.CenterHorizontally

             ) {

                  when (mainViewModel.state) {
                      State.LOADING -> {
                          LoadingSection()

                      }
                      State.FAIL -> {
                          ErrorSection(mainViewModel.errorMessage)

                      }
                      else -> {
                          WeatherSection(mainViewModel.weatherResponse)
                          ForecastSection(mainViewModel.forecastResponse)
                      }
                  }
             }
             FloatingActionButton(
                 onClick = {
                     //Fetch API when location is loaded
                     fetchWeatherInformation(mainViewModel, currentLocation)
                 },
                    modifier = Modifier.padding(bottom = 16.dp)

             ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Add")
             }

         }
       }



        @Composable
       fun ErrorSection(errorMessage : String) {
           return Column(
               modifier = Modifier.fillMaxSize(),
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally

           ){
               Text(text = errorMessage, color = Color.White)
           }
       }

        @Composable
       fun LoadingSection(){
           return Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

           ){
               CircularProgressIndicator(color = Color.White)

           }
       }


       private fun initLocationClient() {
           fusedLocationProviderClient = LocationServices
               .getFusedLocationProviderClient(this)
       }
   }
