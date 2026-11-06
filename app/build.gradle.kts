import java.util.Properties
import java.io.File
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.shishir.aiubportal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shishir.aiubportal"
        minSdk = 29
        targetSdk = 34
        versionCode = 6
        versionName = "1.4.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            /* Signed with the local keystore when gradle-secrets.properties exists
               (kept out of the repo). Without it the release build stays unsigned. */
            val secrets = rootProject.file("gradle-secrets.properties")
            if (secrets.exists()) {
                val props = Properties().also { p -> secrets.inputStream().use { p.load(it) } }
                signingConfig = signingConfigs.create("release") {
                    storeFile = File(props.getProperty("storeFile"))
                    storePassword = props.getProperty("storePassword")
                    keyAlias = props.getProperty("keyAlias")
                    keyPassword = props.getProperty("keyPassword")
                }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("cz.adaptech.tesseract4android:tesseract4android:4.7.0")
}
