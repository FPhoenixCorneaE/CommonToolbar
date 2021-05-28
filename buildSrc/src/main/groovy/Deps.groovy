class Deps {
    static gradle_version = '4.2.0'
    static kotlin_version = '1.5.0'
    static navigation_version = '2.3.5'
    static android_maven_gradle_plugin = '2.1'

    static classpath = [
            gradle      : "com.android.tools.build:gradle:$gradle_version",
            kotlin      : "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version",
            androidMaven: "com.github.dcendents:android-maven-gradle-plugin:$android_maven_gradle_plugin",
    ]

    /* 组件 */
    static FPhoenixCorneaE = [
            CommonUtil    : "com.github.FPhoenixCorneaE:CommonUtil:1.1.1",
            CommonToolbar: 'com.github.FPhoenixCorneaE:CommonToolbar:1.0.6',
    ]

    /** Android */
    static android = [
            compileSdkVersion: 30,
            buildToolsVersion: "30.0.3",
            minSdkVersion    : 21,
            targetSdkVersion : 30,
            versionCode      : 106,
            versionName      : "1.0.6"
    ]

    /** androidX */
    static androidX = [
            appcompat       : "androidx.appcompat:appcompat:1.2.0",
            constraintLayout: "androidx.constraintlayout:constraintlayout:2.0.4",
            activityKtx     : "androidx.activity:activity-ktx:1.2.2",
            fragmentKtx     : "androidx.fragment:fragment-ktx:1.3.3",
    ]

    /** navigation 扩展插件 */
    static navigation = [
            commonKtx  : "androidx.navigation:navigation-common-ktx:$navigation_version",
            fragmentKtx: "androidx.navigation:navigation-fragment-ktx:$navigation_version",
            runtimeKtx : "androidx.navigation:navigation-runtime-ktx:$navigation_version",
            uiKtx      : "androidx.navigation:navigation-ui-ktx:$navigation_version",
    ]

    /** Kotlin */
    static kotlin = [
            coreKtx: "androidx.core:core-ktx:1.3.2",
            stdlib : "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version",
    ]

    /** coroutines */
    static coroutines = [
            core   : "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3",
            android: "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.3"
    ]

    static androidXLibs = androidX.values()
    static navigationLibs = navigation.values()
    static kotlinLibs = kotlin.values()
}