class Deps {
    static gradle_version = '7.0.3'
    static kotlin_version = '1.6.10'

    static classpath = [
            gradle: "com.android.tools.build:gradle:$gradle_version",
            kotlin: "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version",
    ]

    /** Android */
    static android = [
            compileSdkVersion: 31,
            buildToolsVersion: "31.0.0",
            minSdkVersion    : 21,
            targetSdkVersion : 31,
            versionCode      : 302,
            versionName      : "3.0.2"
    ]

    /* 组件 */
    static FPhoenixCorneaE = [
            Common       : "com.github.FPhoenixCorneaE:Common:2.0.9",
            CommonToolbar: "com.github.FPhoenixCorneaE:CommonToolbar:${android.versionName}",
    ]

    /** androidX */
    static androidX = [
            appcompat       : "androidx.appcompat:appcompat:1.4.0",
            coreKtx         : "androidx.core:core-ktx:1.7.0",
            constraintLayout: "androidx.constraintlayout:constraintlayout:2.1.3",
    ]
}