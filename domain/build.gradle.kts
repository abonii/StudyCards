plugins {
    kotlin("android")
    id("com.android.library")
    id("kotlin-parcelize")
}

android {
    namespace = "abm.co.domain"
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(KotlinxDependencies.core)
    implementation(AndroidxDependencies.lifeCycleViewModelKtx)
    TestDependencies.apply {
        testImplementation(junit)
        testImplementation(coroutinesTest)
        testImplementation(mokitoKotlin)
    }
}