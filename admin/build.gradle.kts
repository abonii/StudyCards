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

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    NavigationDependencies.apply {
        implementation(navigationCompose)
        implementation(composeHiltNavigation)
    }
    ComposeDependencies.apply {
        implementation(platform(composeBOM))
        implementation(composeUi)
        implementation(composeUiPreview)
        implementation(composeUiTooling)
        implementation(composeUiTestManifest)
        implementation(composeMaterial)
        implementation(constraintCompose)
        implementation(composeCoil)
        implementation(composeActivity)
        implementation(composeViewModel)
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