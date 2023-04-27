plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "abm.co.designsystem"
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = ComposeDependencies.kotlinCompilerExtensionVersion
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(KotlinxDependencies.json)
    api(MaterialDependencies.main)
    AndroidxDependencies.apply {
        api(coreKtx)
        api(activity)
        api(appcompat)
        api(lifecycleProcess)
        api(lifeCycleRuntimeKtx)
        api(lifeCycleRuntimeCompose)
        api(lifeCycleViewModelKtx)
        api(constraintLayout)
    }
    OtherDependencies.apply {
        api(ssp)
        api(sdp)
        api(apacheLang)
    }
    ComposeDependencies.apply {
        api(platform(composeBOM))
        api(composeUiUtil)
        api(composeUi)
        api(composeUiPreview)
        api(composeUiTooling)
        api(composeUiTestManifest)
        api(composeMaterial)
        api(constraintCompose)
        api(composeCoil)
        api(composeActivity)
        api(composeViewModel)
        api(navigation)
        api(pager)
        api(systemuicontroller)
        api(icons)
    }
}
