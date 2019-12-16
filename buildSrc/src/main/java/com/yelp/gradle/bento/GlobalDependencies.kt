@file:Suppress("Unused")

import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

object Publishing {
    const val GROUP = "com.yelp.android"
    const val VERSION = "15.3.3"
}

object Versions {
    const val COMPILE_SDK = 28
    const val TARGET_SDK = 28
    const val MIN_SDK = 21

    // In alphabetical order.
    const val APACHE_COMMONS = "3.4"
    const val ANDROID_GRADLE = "3.1.4"
    const val ANDROID_X_ANNOTATIONS = "1.0.0"
    const val ANDROID_X_APP_COMPAT = "1.0.0"
    const val ANDROID_X_CONSTRAINT_LAYOUT = "1.1.2"
    const val ANDROID_X_MATERIAL = "1.0.0"
    const val ANDROID_X_RECYCLER_VIEW = "1.0.0"
    const val ANDROID_X_TEST = "1.1.0"
    const val ESPRESSO = "3.1.0"
    const val GRADLE = "4.10"
    const val GUAVA = "25.0-android"
    const val JUNIT = "4.12"
    const val KOTLIN = "1.2.70"
    const val MAVEN_PUBLISH = "3.6.2"
    const val MAVEN_SETTINGS = "0.5"
    const val MOCKITO = "2.7.19"
    const val MOCKITO_KOTLIN = "2.1.0"
    const val POWERMOCK = "1.7.4"
    const val RX_JAVA_2 = "2.1.13"
    const val SUPPORT_TEST = "1.0.2"
}

object BuildScriptLibs {
    const val ANDROID = "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
}

object Libs {
    const val APACHE_COMMONS = "org.apache.commons:commons-lang3:${Versions.APACHE_COMMONS}"
    const val GUAVA = "com.google.guava:guava:${Versions.GUAVA}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.KOTLIN}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val RX_JAVA_2 = "io.reactivex.rxjava2:rxjava:${Versions.RX_JAVA_2}"
}

object PublishLibs {
    const val MAVEN_PUBLISH = "digital.wup:android-maven-publish:${Versions.MAVEN_PUBLISH}"
    const val MAVEN_SETTINGS =
            "net.linguica.gradle:maven-settings-plugin:${Versions.MAVEN_SETTINGS}"
}

object SupportLibs {
    const val ANNOTATIONS = "androidx.annotation:annotation:${Versions.ANDROID_X_ANNOTATIONS}"
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.ANDROID_X_APP_COMPAT}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.ANDROID_X_CONSTRAINT_LAYOUT}"
    const val DESIGN = "com.google.android.material:material:${Versions.ANDROID_X_MATERIAL}"
    const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.ANDROID_X_RECYCLER_VIEW}"
}

object TestLibs {
    const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
    const val ESPRESSO_CONTRIB = "androidx.test.espresso:espresso-contrib:${Versions.ESPRESSO}"
    const val ESPRESSO_IDLING = "androidx.test.espresso.idling:idling-concurrent:${Versions.ESPRESSO}"
    const val ESPRESSO_INTENTS = "androidx.test.espresso:espresso-intents:${Versions.ESPRESSO}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val KOTLIN_JUNIT = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.KOTLIN}"
    const val MOCKITO = "org.mockito:mockito-core:${Versions.MOCKITO}"
    const val MOCKITO_ANDROID = "org.mockito:mockito-android:${Versions.MOCKITO}"
    const val MOCKITO_INLINE = "org.mockito:mockito-inline:${Versions.MOCKITO}"
    const val MOCKITO_KOTLIN = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.MOCKITO_KOTLIN}"
    const val POWERMOCK_JUNIT4 = "org.powermock:powermock-module-junit4:${Versions.POWERMOCK}"
    const val POWERMOCK_JUNIT4_RULE =
            "org.powermock:powermock-module-junit4-rule:${Versions.POWERMOCK}"
    const val POWERMOCK_MOCKITO2 = "org.powermock:powermock-api-mockito2:${Versions.POWERMOCK}"
    const val POWERMOCK_CLASSLOADING =
            "org.powermock:powermock-classloading-xstream:${Versions.POWERMOCK}"
    const val SUPPORT_TEST_RULES = "androidx.test:rules:${Versions.ANDROID_X_TEST}"
    const val SUPPORT_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROID_X_TEST}"
}

object Repos {
    @JvmStatic
    fun MavenArtifactRepository.mavenPlugins() = apply {
        name = "GradlePlugins"
        url = URI("https://plugins.gradle.org/m2/")
    }
}
