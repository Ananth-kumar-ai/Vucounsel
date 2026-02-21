plugins {
    id("com.android.application")
}

android {
    namespace = "org.vignanuniversity.vucounselling_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.vignanuniversity.vucounselling_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "2"

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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // volley library
    implementation("com.android.volley:volley:1.2.1")
    //load profile
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")
    // json image loading
    implementation("com.airbnb.android:lottie:3.7.0")
}
