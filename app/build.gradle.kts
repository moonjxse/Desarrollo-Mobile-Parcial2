plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.tiendapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tiendapp"
        minSdk = 27
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")

    // Material Icons Extended (para iconos)
    implementation("androidx.compose.material:material-icons-extended:1.7.7")

    // Room Database
    implementation("androidx.room:room-runtime:2.7.0-alpha12")
    implementation("androidx.room:room-ktx:2.7.0-alpha12")
    ksp("androidx.room:room-compiler:2.7.0-alpha12")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.0-alpha03")

    // Coil para cargar imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Kotlinx Serialization para JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // NUEVO
    // Animations Compose (transiciones entre pantallas)
    implementation("androidx.compose.animation:animation:1.7.7")

    // DataStore Preferences (para guardar sesión/login)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Accompanist (para permisos de cámara/galería si usas recursos nativos)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Coroutines (para llamadas asíncronas en Room o repositorios)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")


}