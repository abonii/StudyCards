object DIDependencies {
    object Version {
        const val hilt = "2.44.2"
        const val androidHiltCompiler = "1.0.0"
        const val hiltNavigationCompose = "1.0.0"
        const val hiltWork = "1.0.0"
        const val assistedInject = "0.6.0"
        const val dagger2 = "2.44"
    }

    const val hiltAndroid = "com.google.dagger:hilt-android:${Version.hilt}"
    const val dagerHiltCompiler = "com.google.dagger:hilt-compiler:${Version.hilt}"
    const val androidHiltCompiler = "androidx.hilt:hilt-compiler:${Version.androidHiltCompiler}"
    const val hiltNavigationCompose =
        "androidx.hilt:hilt-navigation-compose:${Version.hiltNavigationCompose}"
    const val hiltWork = "androidx.hilt:hilt-work:${Version.hiltWork}"
    const val dagger =  "com.google.dagger:dagger:${Version.dagger2}"
    const val daggerKapt = "com.google.dagger:dagger-compiler:${Version.dagger2}"
    const val assistedInjectAnnotation =
        "com.squareup.inject:assisted-inject-annotations-dagger2:${Version.assistedInject}"
    const val assistedInjectProcessor =
        "com.squareup.inject:assisted-inject-processor-dagger2:${Version.assistedInject}"
}
