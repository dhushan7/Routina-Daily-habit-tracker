plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.example.routina"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.routina"
        minSdk = 24
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
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
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Firebase Crashlytics runtime SDK
//    implementation("com.google.firebase:firebase-crashlytics-ktx:18.5.2")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // MaterialCalendarView
    implementation("com.applandeo:material-calendar-view:1.9.2")

    // MPAndroidChart for mood trend visualization
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Widget support (Glance)
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // Sensor and step counter (core-ktx already included)
    // implementation("androidx.core:core-ktx:1.12.0") <- redundant, already included above

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")

}
