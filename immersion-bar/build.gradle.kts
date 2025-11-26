plugins {
    alias(libs.plugins.xeonyu.library)
    alias(libs.plugins.vanniktechPublish)
    alias(libs.plugins.kotlin.android)
}

mavenPublishing {
    // 发布到 Maven Central
    publishToMavenCentral()
    // 显式启用签名
    signAllPublications()
}

android {
    namespace = "com.yzq.immersionbar"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.kotlin.bom.stable))
    implementation(libs.androidx.appcompat.stable)
    implementation(libs.androidx.activity.stable)
    implementation(libs.androidx.activity.ktx.stable)
}

