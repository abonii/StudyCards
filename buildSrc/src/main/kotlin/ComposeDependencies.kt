object ComposeDependencies {
    object Version {
        const val composeBOM = "2023.01.00"
        const val composeActivity = "1.6.1"
        const val composeCoil = "2.2.2"
        const val accompanist = "0.24.11-rc"
        const val composeViewModel = "2.5.1"
        const val constraintCompose = "1.0.1"
        const val pagingCompose = "1.0.0-alpha18"
    }

    const val kotlinCompilerExtensionVersion = "1.4.3"
    const val composeBOM = "androidx.compose:compose-bom:${Version.composeBOM}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest"
    const val composeMaterial = "androidx.compose.material:material"
    const val composeActivity = "androidx.activity:activity-compose:${Version.composeActivity}"
    const val composeCoil = "io.coil-kt:coil-compose:${Version.composeCoil}"
    const val constraintCompose =
        "androidx.constraintlayout:constraintlayout-compose:${Version.constraintCompose}"
    const val accompanistWebview =
        "com.google.accompanist:accompanist-webview:${Version.accompanist}"
    const val composeViewModel =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.composeViewModel}"
    const val pagingCompose = "androidx.paging:paging-compose${Version.pagingCompose}"
}