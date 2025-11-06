plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt") // 👈 Necesario para Room
}

android {
    namespace = "com.tallerproyectos.encartacusquena"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tallerproyectos.encartacusquena"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // ajusta según tu versión de Kotlin
    }
}

dependencies {
    // Dependencias base de Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ✅ Navegación Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // ✅ Room (base de datos local)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ✅ ViewModel y LiveData (opcional pero útil)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    // ya tenías compose bom
    implementation("androidx.navigation:navigation-compose:2.5.3")
// navegación compose
    implementation("androidx.activity:activity-compose:1.6.1")
// activity compose
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
// runtime
// (la rest de tus dependencias Compose / Material3 ya están)
    dependencies {
        // ... tus dependencias actuales

        // Coil para cargar imágenes
        implementation("io.coil-kt:coil-compose:2.5.0")

        // Para VideoView (ya viene incluido en Android SDK, no necesitas agregar nada extra)
    }

    // Pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)






}
