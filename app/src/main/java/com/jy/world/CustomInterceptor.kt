package com.jy.world

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class CustomInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.OPEN_WEATHER_API_KEY}")  // API 키를 Authorization 헤더로 추가
            .build()
        val response = chain.proceed(request)

        // 응답이 실패한 경우 처리 (예: 상태 코드가 200이 아니면 예외 처리)
        if (!response.isSuccessful) {
            throw IOException("Unexpected code ${response.code}")
        }

        // 응답 바디가 null인지 확인
        val responseBody = response.body
        if (responseBody == null) {
            throw IOException("Empty response body")
        }

        return response

    }
}
// Retrofit 객체 생성
fun createRetrofit(): Retrofit {

    val client = OkHttpClient.Builder()
        .addInterceptor(CustomInterceptor())  // Interceptor 추가
        .build()

    return Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")  // OpenWeatherMap API base URL
        .client(client)  // OkHttpClient 적용
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}