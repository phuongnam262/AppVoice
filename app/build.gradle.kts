plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "gmo.demo.voidtask"
    compileSdk = 35

    defaultConfig {
        applicationId = "gmo.demo.voidtask"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            storeFile = file("my-release-key.jks")
            storePassword = "password"
            keyAlias = "my_key_alias"
            keyPassword = "password"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
        }
        create("develop") {
            initWith(getByName("debug")) // Inherit from debug
            isDebuggable = true
            applicationIdSuffix = ".dev"
            buildConfigField("String", "API_URL", "\"http://118.69.77.23:3002/\"")
        }
        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"
            buildConfigField("String", "API_URL", "\"http://118.69.77.23:3002/\"")
        }
        create("honban") {
            initWith(getByName("release"))
            buildConfigField("String", "API_URL", "\"http://118.69.77.23:3002/\"")
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
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.preference.ktx)
    implementation (libs.androidx.preference.ktx.v120)

    //Retrofit and GSON
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.converter.moshi)

    //Android Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    //Kodein Dependency Injection
    implementation(libs.kodein.di.generic.jvm)
    implementation(libs.kodein.di.framework.android.x)

    //Android Navigation Architecture
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx.v277)
    implementation(libs.androidx.navigation.ui.ktx.v277)

    //Groupie
    implementation (libs.github.groupie)
    implementation (libs.groupie.viewbinding)
    implementation (libs.github.groupie.databinding)

    //Glide
    implementation (libs.glide)
    ksp(libs.compiler)

    // encrypt
    implementation (libs.androidx.security.crypto)

}