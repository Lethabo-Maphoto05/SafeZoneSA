plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Uncomment once google-services.json (from Firebase console) has been added to app/
    id("com.google.gms.google-services")
}

android {
    namespace = "com.groupeight.safezonesa"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.groupeight.safezonesa"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-part1-prototype"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Base URL for the ASP.NET Core REST API (see Section 5 of the Planning & Design doc).
        // Overridden per build type below — debug talks to your local dotnet run instance,
        // release talks to the deployed Azure App Service. This is just the fallback.
        buildConfigField("String", "API_BASE_URL", "\"https://safezone-sa-api-2026-dffnf6ahhtchcxcn.centralindia-01.azurewebsites.net/\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    buildTypes {
        debug {
            // 10.0.2.2 is the Android emulator's alias for your PC's localhost — it is NOT
            // a typo and won't work on a physical device. If you're running on a real phone
            // on the same Wi-Fi as your PC, replace this with your PC's LAN IP instead, e.g.
            // "http://192.168.1.42:5080/" (run `ipconfig` on Windows to find it), and make
            // sure `dotnet run` is actually listening on that interface — see the note in
            // Properties/launchSettings.json on the API side if it only binds to localhost.
            buildConfigField("String", "API_BASE_URL", "\"https://safezone-sa-api-2026-dffnf6ahhtchcxcn.centralindia-01.azurewebsites.net/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Real deployment target once it's live on Azure App Service (Section 5.5).
            buildConfigField("String", "API_BASE_URL", "\"https://safezone-sa-api-2026-dffnf6ahhtchcxcn.centralindia-01.azurewebsites.net/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core / Compose
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Firebase (Cloud Messaging for FR12 geo-targeted push, Auth for FR1, Firestore for local caching)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Networking (talks to the ASP.NET Core REST API described in Section 5)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Maps (Safe Route / Crime Map — FR7, FR13)
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:4.4.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Coil for image loading (incident photos, missing-person photos)
    implementation("io.coil-kt:coil-compose:2.6.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
