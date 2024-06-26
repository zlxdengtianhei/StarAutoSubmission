plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.starautosubmission"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.starautosubmission"
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
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("io.github.bmob:android-sdk:3.9.4")
    implementation ("io.reactivex.rxjava2:rxjava:2.2.8")
    implementation ("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation ("com.squareup.okhttp3:okhttp:4.8.1")
    implementation ("com.squareup.okio:okio:2.2.2")
    implementation ("com.google.code.gson:gson:2.8.5")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")



}