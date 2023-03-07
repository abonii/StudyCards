plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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