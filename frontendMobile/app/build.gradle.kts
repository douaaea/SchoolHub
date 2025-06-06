plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.schoolapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.schoolapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = false // Disable to avoid binding errors
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.viewpager)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.mlkit.subject.segmentation)
    implementation(libs.cardview)
    implementation(libs.firebase.inappmessaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.6")
    // AndroidX Libraries
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Material Design Components
    implementation ("com.google.android.material:material:1.11.0")

    // Retrofit and Gson for API calls
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0") // Optional: for debugging

    // Lifecycle (optional but recommended)
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}