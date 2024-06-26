plugins {
    kotlin("android")
    kotlin("kapt")
    id("com.android.library")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
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
    implementation(project(":library:designsystem"))
    implementation(project(":library:permissions"))
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(BillingDependencies.billing)
    implementation(files("libs/epublib-core-latest.jar"))
    implementation(files("libs/jsoup-1.14.3.jar"))
    implementation(files("libs/slf4j-android-1.6.1-RC1.jar"))
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
    KotlinxDependencies.forStudyCards().forEach(::implementation)
    NavigationDependencies.apply {
        api(navigationCompose)
        api(fragment)
        api(ui)
        api(runtime)
    }
}

kapt {
    correctErrorTypes = true
}