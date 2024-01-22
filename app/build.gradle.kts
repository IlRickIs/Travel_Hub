
plugins {
    id("com.android.application")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "it.unimib.travelhub"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.unimib.travelhub"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.datastore:datastore-preferences-rxjava2:1.0.0")
    val navVersion = "2.7.5"

    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("commons-validator:commons-validator:1.7")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-rxjava2:1.0.0")
    implementation("androidx.datastore:datastore-rxjava3:1.0.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}



