plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = studyCardsProjectApplicationId
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        applicationId = studyCardsProjectApplicationId
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion
        versionCode = studyCardsProjectVersionCode
        versionName = studyCardsProjectVersionName

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

    signingConfigs {
        create("release") {
            keyAlias = "STUDY_CARDS_ALIAS"
            storePassword = "STUDY_CARDS_PASSWORD"
            keyPassword = "STUDY_CARDS_PASSWORD"
            storeFile = file("/Users/abylay/StudioProjects/StudyCards/key.jsk")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "@string/app_name_dev"
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "VERSION_CODE", "\"${defaultConfig.versionCode}\"")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["appName"] = "@string/app_name"
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "VERSION_CODE", "\"${defaultConfig.versionCode}\"")
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
        dataBinding = true
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
}