plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.miniproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.miniproject"
        minSdk = 21
        targetSdk = 21
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("io.github.muxiaolin:org.eclipse.paho.android.service:1.1.4")
    implementation ("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0")
    implementation ("com.github.angads25:toggle:1.1.0")
}