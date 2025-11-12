// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // agregados
    // Plugin de Kotlin Serialization (para manejo de JSON)
    kotlin("plugin.serialization") version "2.1.0" apply false

    // KSP (para procesar anotaciones de Room)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false


}