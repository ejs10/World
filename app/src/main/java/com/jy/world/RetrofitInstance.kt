package com.jy.world

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://api.openweathermap.org/"

    // HttpLoggingInterceptor로 로그 출력
    // 인터셉터 설정
    private fun  getOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY) // 요청/응답의 본문을 로그로 출력

        /**
        // OkHttpClient에 Interceptor 추가
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // 로그 출력용 Interceptor
            .addInterceptor(CustomInterceptor())  // CustomInterceptor 추가 (API 키를 Authorization 헤더에 추가)
            .build()

        **/
        // OkHttpClient에 Interceptor 추가
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // CustomInterceptor 추가
            .addInterceptor { chain ->
                val originalRequest: Request = chain.request()
                val apiKey = BuildConfig.OPEN_WEATHER_API_KEY
                val requestWithApiKey = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $apiKey")  // API 키를 헤더에 추가
                    .build()
                chain.proceed(requestWithApiKey)
            }
            .build()
    }



    fun createRetrofit(): Retrofit {
        val client = getOkHttpClient()  // OkHttpClient 가져오기

        return Retrofit.Builder()
            .baseUrl(BASE_URL)  // OpenWeatherMap API base URL
            .client(client)  // OkHttpClient 적용
            .addConverterFactory(GsonConverterFactory.create())  // Gson 변환기 사용
            .build()
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(CustomInterceptor())  // Interceptor 추가
        .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃 설정
        .readTimeout(30, TimeUnit.SECONDS)  // 읽기 타임아웃 설정
        .build()


    val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // OkHttpClient 연결
            .addConverterFactory(GsonConverterFactory.create()) //Gson을 사용하여 응답을 파싱
            .build()
    }


}