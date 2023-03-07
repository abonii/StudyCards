object KotlinxDependencies {
    object Versions {
        const val kotlin = "1.8.10"
        const val coroutines = "1.5.0"
        const val kotlinSerialization = "1.0.1"
        const val collectionsImmutable = "0.3.5"
    }

    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val play = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.coroutines}"
    const val json =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
    const val immutable =
        "org.jetbrains.kotlinx:kotlinx-collections-immutable:${Versions.collectionsImmutable}"

    fun forStudyCards() = listOf(core, android, json, immutable)
}