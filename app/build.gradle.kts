plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltKotlin)

    id ("kotlin-kapt")
}

android {
    namespace = "com.utilitytoolbox.qrscanner.barcodescanner"
    compileSdk = 34

    defaultConfig {
        applicationId = "cm.utilitytoolbox.qrscanner.barcodescanner"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val lifecycle_version = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-process:$lifecycle_version")
    kapt ("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")


    // Room
    val roomVersion = "2.4.2"
    kapt ("androidx.room:room-compiler:$roomVersion")
    implementation ("androidx.room:room-runtime:$roomVersion")
    implementation ("androidx.room:room-rxjava2:$roomVersion")

    // Paging
    val pagingVersion = "1.0.1"
    implementation ("android.arch.paging:runtime:$pagingVersion")
    implementation ("android.arch.paging:rxjava2:$pagingVersion")

    // Barcode Image Generator
    //noinspection GradleDependency
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    // VCard Parser
    implementation ("com.googlecode.ez-vcard:ez-vcard:0.11.0")

    // Base32 Encoder
    implementation  ("commons-codec:commons-codec:1.15")

    // Rx
    implementation ("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation ("com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:2.2.0")
    // Image Crop Library
    implementation ("com.isseiaoki:simplecropview:1.1.8")


    // CameraX core library using the camera2 implementation
    val camerax_version = "1.2.3"
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation ("androidx.camera:camera-view:${camerax_version}")

    //lottie animation
    implementation ("com.airbnb.android:lottie:6.1.0")

    implementation ("androidx.work:work-runtime-ktx:2.7.0")

    //billing library
    implementation ("com.github.akshaaatt:Google-IAP:1.6.0")

    implementation ("com.google.mlkit:vision-common:17.3.0")
    implementation ("androidx.multidex:multidex:2.0.1")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation ("androidx.browser:browser:1.4.0")

    //Permission Library
    implementation ("com.guolindev.permissionx:permissionx:1.7.1")


    implementation ("com.google.dagger:hilt-android:2.44")
    kapt ("com.google.dagger:hilt-compiler:2.44")

    implementation ("com.github.doyaaaaaken:kotlin-csv-jvm:0.9.0")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation ("com.github.wdsqjq:AndRatingBar:1.0.6")

}