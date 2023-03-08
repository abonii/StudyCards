plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "abm.co.feature"
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_SERVER_ID",
                "${project.properties["GOOGLE_SIGN_IN_SERVER_ID"]}"
            )
        }
        release {
            isMinifyEnabled = true
            buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_SERVER_ID",
                "${project.properties["GOOGLE_SIGN_IN_SERVER_ID"]}"
            )
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ComposeDependencies.kotlinCompilerExtensionVersion
    }
}

dependencies {
    api(project(":library:designsystem"))
    api(project(":domain"))
    DIDependencies.apply {
        implementation(hiltAndroid)
        implementation(hiltNavigationCompose)
        kapt(dagerHiltCompiler)
    }
    FirebaseDependencies.apply {
        implementation(platform(firebaseBOM))
        implementation(auth)
        implementation(analytics)
        implementation(firebaseAuth)
        implementation(database)
    }
}

kapt {
    correctErrorTypes = true
}