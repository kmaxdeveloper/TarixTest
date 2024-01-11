plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "uz.kmax.tarixtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "uz.kmax.tarixtest"
        minSdk = 24
        targetSdk = 34
        versionCode = 7
        versionName = "1.3.5"

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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-storage-ktx:20.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.kmaxdeveloper:BaseLibrary:1.1.2")
    implementation("com.google.android.gms:play-services-ads:22.3.0")
    implementation("com.google.android.play:review-ktx:2.0.1")
    implementation("nl.dionsegijn:konfetti-xml:2.0.3")
    implementation("nl.dionsegijn:konfetti:1.3.2")
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}