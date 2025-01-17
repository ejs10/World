plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.jy.world"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jy.world"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Release 빌드에만 적용될 API 키 설정
        buildConfigField("String", "OPEN_WEATHER_API_KEY", "\"${project.findProperty("OPEN_WEATHER_API_KEY")?: "default_api_key"}\"")


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true



            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        // buildConfig 기능 활성화
        buildFeatures {
            buildConfig = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
         jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //세계날씨

    implementation(libs.androidx.core.ktx.v190 )
    implementation(libs.androidx.appcompat)
    implementation(libs.material.v190)
    implementation(libs.androidx.constraintlayout.v214)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor.v4100)

    //implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation(libs.kotlinx.coroutines.android)


    implementation(libs.glide)
    annotationProcessor(libs.compiler)
}