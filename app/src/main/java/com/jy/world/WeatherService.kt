package com.jy.world

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String, //도시이름
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY, //API 키
        @Query("units") units: String = "metric" //온도단위
    ): WeatherResponse // 응답을 WeatherResponse 클래스로 받음

    // OpenWeatherMap API 호출 (위치 기반 날씨 정보 요청)
    @GET("onecall")
    suspend fun getWeatherByLocation(
        @Query("lat") lat: Double,       // 위도
        @Query("lon") lon: Double,       // 경도
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY,  // API 키
        @Query("units") units: String = "metric" // 섭씨 단위
    ): WeatherResponse

}



