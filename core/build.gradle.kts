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

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["appName"] = "@string/app_name_dev"
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
            buildConfigField("String", "VERSION_CODE", "\"${defaultConfig.versionCode}\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    FirebaseDependencies.apply {
        implementation(platform(firebaseBOM))
        implementation(auth)
        implementation(databaseDatabase)
        implementation(crashlytics)
        implementation(functions)
        implementation(firestore)
        implementation(database)
        implementation(firebaseAuth)
        implementation(analytics)
    }
    NavigationDependencies.apply {
        implementation(navigationCompose)
        implementation(composeHiltNavigation)
    }
    TestDependencies.apply {
        testImplementation(junit)
        androidTestImplementation(junitExt)
    }
    AndroidxDependencies.apply {
        implementation(splashScreen)
        implementation(dataStore)
        implementation(dataStorePreference)
        implementation(lifecycleProcess)
        implementation(lifeCycleRuntimeKtx)
        implementation(lifeCycleRuntimeCompose)
        implementation(lifeCycleViewModelKtx)
    }
    KotlinxDependencies.forStudyCards().forEach(::implementation)
    OtherDependencies.forStudyCards().forEach(::implementation)
    implementation(BillingDependencies.billing)
    implementation(StartupDependencies.startupRuntime)
    implementation(project(":library:designsystem"))
}