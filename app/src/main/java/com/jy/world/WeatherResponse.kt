package com.jy.world

data class WeatherResponse(
    val city: City,
    val list: List<WeatherData>
){
    // list가 null이 아니고 비어 있지 않으면 true를 반환
    fun isValid(): Boolean {
        return list != null && list.isNotEmpty()
    }
}

data class City(
    val name: String
)

data class WeatherData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class WeatherDetails(
    val description: String,
    val icon: String // 아이콘 정보 (예: 10d, 01d 등)
)

data class Weather(
    val description: String,  //날씨설명
    val temp: Double,
    val icon: String
) {
    fun getIconUrl(): String {
        return "https://openweathermap.org/img/wn/$icon@2x.png"
    }
}

