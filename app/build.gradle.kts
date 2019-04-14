plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.cilenco.skiptrack"
        minSdkVersion(26)
        targetSdkVersion(28)
        versionName = "1.0.4"
        versionCode = 5
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //implementation("androidx.preference:preference:1.0.0")
    implementation("com.android.support:preference-v7:28.0.0")
    implementation("net.grandcentrix.tray:tray:0.12.0")
    implementation(embeddedKotlin("stdlib-jdk8"))
}

repositories {
    mavenCentral()
}
