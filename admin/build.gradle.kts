plugins {
    kotlin("kapt")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = studyCardsAdminProjectApplicationId
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        applicationId = studyCardsAdminProjectApplicationId
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion
        versionCode = studyCardsAdminProjectVersionCode
        versionName = studyCardsAdminProjectVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "VERSION_CODE", "\"${defaultConfig.versionCode}\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ComposeDependencies.kotlinCompilerExtensionVersion
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    coreLibraryDesugaring(DesugaringDependencies.desugarJdkLibs)
    DIDependencies.apply {
        kapt(dagerHiltCompiler)
        kapt(androidHiltCompiler)
        implementation(hiltAndroid)
        implementation(hiltNavigationCompose)
//        implementation(assistedInjectAnnotation)
//        implementation(assistedInjectProcessor)
    }
    TestDependencies.apply {
        testImplementation(junit)
        androidTestImplementation(junitExt)
    }
    AndroidxDependencies.apply {
        implementation(splashScreen)
    }
    KotlinxDependencies.forStudyCards().forEach(::implementation)
    OtherDependencies.forStudyCards().forEach(::implementation)
    implementation(BillingDependencies.billing)
    implementation(StartupDependencies.startupRuntime)
    implementation(project(":library:designsystem"))
    implementation(project(":feature"))
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))
}