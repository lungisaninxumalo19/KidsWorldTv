plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.kidsworldtv.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kidsworldtv.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 17
        versionName = "0.1.0"
    }

compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = "17"
}

    buildFeatures { compose = true }
}
