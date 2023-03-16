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
    implementation(project(":domain"))
    implementation(KotlinxDependencies.json)
    AndroidxDependencies.apply {
        api(coreKtx)
        api(activity)
        api(appcompat)
        api(lifeCycleRuntimeCompose)
        api(lifeCycleViewModelKtx)
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
