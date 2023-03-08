object FirebaseDependencies {

    object Versions {
        const val playAuth = "20.4.1"
        const val firebaseBom = "30.0.0"
    }

    const val auth = "com.google.android.gms:play-services-auth:${Versions.playAuth}"
    const val firebaseBOM = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"
    const val analytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebaseAuth = "com.google.firebase:firebase-auth-ktx"
    const val database = "com.google.firebase:firebase-database-ktx"
    const val firestore = "com.google.firebase:firebase-firestore-ktx"
    const val functions = "com.google.firebase:firebase-functions-ktx"
    const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
}