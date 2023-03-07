plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
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
        debug {
            buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_SERVER_ID",
                "${project.properties["GOOGLE_SIGN_IN_SERVER_ID"]}"
            )
        }
        release {
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
}

dependencies {
    implementation(project(":domain"))
    implementation(BillingDependencies.billing)
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
    TestDependencies.apply {
        testImplementation(junit)
        androidTestImplementation(junitExt)
        androidTestImplementation(coroutinesTest)
    }
}

kapt {
    correctErrorTypes = true
}