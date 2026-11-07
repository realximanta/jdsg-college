import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Load local.properties for local builds
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

// Fetch Web Client ID
val webClientId = System.getenv("WEB_CLIENT_ID")?.takeIf { it.isNotBlank() } 
    ?: localProperties.getProperty("WEB_CLIENT_ID") 
    ?: "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"

// Fetch Keystore Credentials (Env Vars for CI, local.properties for Local)
val keystorePath = System.getenv("KEYSTORE_PATH")?.takeIf { it.isNotBlank() } ?: localProperties.getProperty("KEYSTORE_PATH")
val keystorePassword = System.getenv("KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() } ?: localProperties.getProperty("KEYSTORE_PASSWORD")
val keyAlias = System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() } ?: localProperties.getProperty("KEY_ALIAS")
val keyPassword = System.getenv("KEY_PASSWORD")?.takeIf { it.isNotBlank() } ?: localProperties.getProperty("KEY_PASSWORD")

android {
    // FIXED: Removed the reserved 'in' keyword from the package namespace
    namespace = "edu.jdsgcollege.bokakhat"
    
    compileSdk = 34

    defaultConfig {
        // KEPT: Google OAuth requires the applicationId to match your domain
        applicationId = "in.edu.jdsgcollege.bokakhat"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")
    }

    signingConfigs {
        create("release") {
            // Only configure signing if all fields are present and not empty
            if (!keystorePath.isNullOrBlank() && !keystorePassword.isNullOrBlank() && !keyAlias.isNullOrBlank() && !keyPassword.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Apply signing config only if it was successfully configured
            if (!keystorePath.isNullOrBlank() && !keystorePassword.isNullOrBlank() && !keyAlias.isNullOrBlank() && !keyPassword.isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
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
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
}
