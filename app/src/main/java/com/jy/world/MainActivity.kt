package com.jy.world

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.sql.Date

class MainActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = BuildConfig.OPEN_WEATHER_API_KEY // OpenWeatherMap에서 발급받은 API 키

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // activity_main.xml 레이아웃 설정

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val cityEditText = findViewById<EditText>(R.id.cityEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val weatherTextView = findViewById<TextView>(R.id.weatherTextView)


        val latitude = 35.6764
        val longitude = 139.6500


        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            getCurrentLocation()  // 권한이 있으면 위치 정보 가져오기
        }

        // 도시명 입력 후 날씨 정보 요청
        submitButton.setOnClickListener {
            val cityName = cityEditText.text.toString().trim()
            if (cityName.isNotEmpty()) {
                // Coroutine을 사용하여 fetchWeather 호출
                lifecycleScope.launch {
                    try {
                        val weatherService = RetrofitInstance.retrofitInstance.create(WeatherService::class.java)
                        val weatherData = weatherService.getWeatherByCity(cityName, apiKey)

                        // 날씨 데이터가 null이 아닌지 체크
                        if(weatherData != null && weatherData.list?.isNotEmpty() == true) {
                            updateWeatherUI(weatherData, weatherTextView) // 날씨 데이터를 UI에 반영
                        }else{
                            Toast.makeText(this@MainActivity, "Weather data is empty or invalid", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Failed to fetch weather: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        // Edge-to-edge UI 설정 (시스템 바에 맞게 여백 조정)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 현재 위치를 가져오는 함수
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        // 위치 정보를 사용하여 날씨 정보 요청 (예: 위도, 경도)
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // 날씨 API에 위치 정보 전달
                        fetchWeatherData(latitude, longitude)
                    } else {
                        Toast.makeText(this, "위치를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // 날씨 API 호출 (위치 정보 전달)
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val weatherService = RetrofitInstance.retrofitInstance.create(WeatherService::class.java)

        lifecycleScope.launch {
            try {
                val weatherData = weatherService.getWeatherByLocation(latitude, longitude, apiKey)
                // weatherTextView도 함께 전달
                val weatherTextView = findViewById<TextView>(R.id.weatherTextView)
                updateWeatherUI(weatherData, weatherTextView)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Failed to fetch weather by location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val weatherService = RetrofitInstance.retrofitInstance.create(WeatherService::class.java)

        weatherService.getWeatherByLocation(latitude, longitude, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    if (weatherData != null) {
                        // 날씨 데이터 처리
                        updateWeatherUI(weatherData)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Weather data fetch failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to fetch weather: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    **/

    // 도시명을 입력받아 날씨 정보 요청
    private suspend fun fetchWeather(cityName: String, weatherTextView: TextView) {
        val weatherService = RetrofitInstance.retrofitInstance.create(WeatherService::class.java)

        try {
            // 도시명으로 날씨 데이터를 요청
            val weatherData = weatherService.getWeatherByCity(cityName, apiKey)
            // 날씨 데이터가 유효하면 UI 업데이트
            updateWeatherUI(weatherData, weatherTextView)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Failed to fetch weather: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    // 날씨 UI 업데이트 함수
    private fun updateWeatherUI(weatherData: WeatherResponse, weatherTextView: TextView ){
        val todayWeather = getWeatherForDate(weatherData, 0) // 오늘 날씨
        val tomorrowWeather = getWeatherForDate(weatherData, 1) // 내일 날씨
        val weekLaterWeather = getWeatherForDate(weatherData, 7) // 1주일 후 날씨

        // UI 업데이트
        val weatherInfo = """
            Today's Weather: Temp: ${"%.1f".format(todayWeather.temp)}°C, Description: ${todayWeather.description}
            Tomorrow's Weather: Temp: ${"%.1f".format(tomorrowWeather.temp)}°C, Description: ${tomorrowWeather.description}
            Weather for 1 Week Later: Temp: ${"%.1f".format(weekLaterWeather.temp)}°C, Description: ${weekLaterWeather.description}
        """.trimIndent()

       //val weatherTextView = findViewById<TextView>(R.id.weatherTextView)
        weatherTextView.text = weatherInfo
    }


    // 특정 날짜에 해당하는 날씨 정보 추출
    private fun getWeatherForDate(weatherData: WeatherResponse, daysAhead: Int): Weather {
        val targetTime = getTargetTime(daysAhead)

        val closestForecast = weatherData.list.minByOrNull {
            val forecastDate = Date(it.dt * 1000L)
            Math.abs(forecastDate.time - targetTime)

        }
        return closestForecast?.let {
            val weatherDescription = it.weather.firstOrNull()?.description ?: "No description"
            Weather(weatherDescription, it.main.temp, it.weather.firstOrNull()?.icon ?: "")
        } ?: Weather("No Data", 0.0, "")
    }

    // 특정 날짜의 Unix timestamp 계산 (오늘, 내일, 일주일 후)
    private fun getTargetTime(daysAhead: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, daysAhead)  // daysAhead에 맞춰 날짜를 변경
        return calendar.timeInMillis
    }




}



