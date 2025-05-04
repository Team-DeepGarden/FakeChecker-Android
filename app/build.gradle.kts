plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.deepfake"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.deepfake"
        minSdk = 29
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {

    implementation("org.ow2.asm:asm:9.7")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.gms:google-services:4.3.15")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.amazonaws:aws-android-sdk-s3:2.22.5")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")
    ksp("com.github.bumptech.glide:ksp:4.14.2")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okio:okio:3.2.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.ar.sceneform:filament-android:1.17.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}