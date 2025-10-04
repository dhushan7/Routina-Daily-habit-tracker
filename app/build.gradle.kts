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
    // AndroidX & Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)

    // Firebase Crashlytics build tools are usually a Gradle plugin, not a runtime dependency.
    // If you actually need the runtime SDK, use 'com.google.firebase:firebase-crashlytics-ktx'.
    implementation(libs.firebase.crashlytics.buildtools)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // MaterialCalendarView
    implementation("com.applandeo:material-calendar-view:1.9.2")

    // MPAndroidChart — pick ONE of the two options below:

    // (A) Using your version catalog (make sure libs.versions.toml defines it correctly)
    implementation(libs.mpandroidchart)

    // (B) Or direct dependency (works without catalog, since we added JitPack in settings.gradle.kts)
    // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
