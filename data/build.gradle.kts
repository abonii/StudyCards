plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android {
    namespace = "abm.co.data"
    compileSdk = projectCompileSdkVersion

    defaultConfig {
        minSdk = projectMinSdkVersion
        targetSdk = projectTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
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
    implementation(project(":domain"))
    implementation(BillingDependencies.billing)
    implementation(KotlinxDependencies.json)
    debugApi(PlutoDependencies.pluto)
    releaseApi(PlutoDependencies.plutoNoOp)
    DIDependencies.apply {
        implementation(hiltAndroid)
        kapt(dagerHiltCompiler)
    }
    NetworkDependencies.apply {
        implementation(retrofit)
        implementation(gson)
        implementation(loggingInterceptor)
        debugImplementation(chucker)
        releaseImplementation(chuckerNoOp)
    }
    FirebaseDependencies.apply {
        api(platform(firebaseBOM))
        api(crashlytics)
        api(functions)
        api(firestore)
        api(database)
        api(analytics)
        api(firebaseAuth)
    }
    AndroidxDependencies.apply {
        implementation(dataStore)
        implementation(dataStorePreference)
    }
    TestDependencies.apply {
        testImplementation(junit)
        androidTestImplementation(junitExt)
        androidTestImplementation(coroutinesTest)
    }
}

kapt {
    correctErrorTypes = true
}