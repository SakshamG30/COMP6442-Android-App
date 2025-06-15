plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.g11_group_application"
    compileSdk = 33


    defaultConfig {
        applicationId = "com.example.g11_group_application"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.activity:activity:1.3.1")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("com.github.lecho:hellocharts-library:1.5.8@aar")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    // Test dependencies
    implementation("junit:junit:4.12")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.11.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.4.0")
    testImplementation("androidx.test:core:1.4.0")
    // JSON Converter dependencies
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.opencsv:opencsv:5.9")
    implementation("org.json:json:20240303")
    // Firebase dependencies
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    implementation("com.google.firebase:firebase-storage:19.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database")
    implementation("io.reactivex.rxjava3:rxjava:3.1.3")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
}