plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.gogo.kotlinbtk"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.gogo.kotlinbtk"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding=true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }



}


dependencies {

    //navbar eklentileri
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.material.v1120)





    implementation(libs.jackson.module.kotlin)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.okhttp)



    implementation(libs.jackson.databind)




    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.coordinatorlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.gson)
    implementation (libs.androidx.material3)
    implementation (libs.androidx.ui)
    implementation (libs.androidx.activity.compose)
    implementation (libs.gson.v210)
    implementation (libs.androidx.material3.v110alpha01)
    implementation (libs.androidx.activity.compose.v170alpha03)
    implementation(libs.material.v140)
    implementation (libs.material.v1120)

    implementation (libs.core.ktx.v1120)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation (libs.androidx.activity.compose.v170)
    implementation (platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose.v161)
    implementation(libs.material.v120)
    implementation(libs.androidx.appcompat.v170)
    implementation(libs.material.v150)
    implementation(libs.androidx.constraintlayout.v220)

    androidTestImplementation(libs.ui.test.manifest)

    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.v176)
    androidTestImplementation(libs.androidx.ui.test.manifest.v176)
    androidTestImplementation(libs.androidx.ui.test.junit4.v176)

// Material Design
    implementation(libs.material.v190)

    // AndroidX Activity
    implementation(libs.androidx.activity.ktx)


    // Google Play Services Base
    implementation(libs.play.services.base)

    // Google Play Services Basement
    implementation(libs.play.services.basement)



    // OkHttp
    implementation(libs.okhttp)

    // Jackson Databind
    implementation(libs.jackson.databind)


    // Kotlin Standard Library
    implementation(libs.kotlin.stdlib)

    // Kotlin Reflect
    implementation(libs.kotlin.reflect)

    // Jackson Kotlin Module
    implementation(libs.jackson.module.kotlin)






}