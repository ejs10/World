# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# 모든 클래스와 메소드 이름을 난독화 (기본적인 난독화)
-keep class com.jy.world.** { *; }
-dontwarn com.jy.world.**

# Gson에서 사용하는 클래스는 난독화하지 않음
-keep class com.google.gson.** { *; }

# OkHttp와 관련된 클래스는 난독화하지 않음
-keep class com.squareup.okhttp3.** { *; }

# 외부 라이브러리나 중요한 메소드들이 난독화되지 않도록 예외 처리
-keepclassmembers class * {
    public <methods>;
}

# JSON 파싱 시 SerializedName 어노테이션이 적용된 필드는 난독화되지 않도록 설정
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}



# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile