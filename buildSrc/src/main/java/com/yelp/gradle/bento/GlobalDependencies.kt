@file:Suppress("Unused")

import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

object Publishing {
    const val GROUP = "com.yelp.android"
    const val VERSION = "19.1.0_LazyListState"
}

object Versions {
    const val COMPILE_SDK = 33
    const val TARGET_SDK = 33
    const val MIN_SDK = 21

    // In alphabetical order.
    const val APACHE_COMMONS = "3.4"
    const val ANDROID_GRADLE = "7.4.2"
    const val ANDROID_X_APP_COMPAT = "1.0.0"
    const val ANDROID_X_CONSTRAINT_LAYOUT = "1.1.2"
    const val ANDROID_X_CORE_CTX = "1.4.0"
    const val ANDROID_X_LIFECYCLE = "2.3.1"
    const val ANDROID_X_MATERIAL = "1.0.0"
    const val ANDROID_X_RECYCLER_VIEW = "1.3.0"
    const val ANDROID_X_TEST = "1.1.0"
    const val ANDROID_X_VIEW_PAGER2 = "1.0.0"
    const val COMPOSE = "1.4.0"
    const val COMPOSE_KOTLIN_COMPILER = "1.4.4"
    const val COROUTINES = "1.4.0"
    const val ESPRESSO = "3.1.0"
    const val GRADLE = "7.5.1"
    const val GUAVA = "28.1-android"
    const val JUNIT = "4.12"
    const val KOTLIN = "1.8.10"
    const val MAVEN_SETTINGS = "0.5"
    const val MOCKITO = "3.11.2"
    const val MOCKITO_KOTLIN = "2.1.0"
    const val ROBOLECTRIC = "4.7.3"
    const val RX_JAVA_3 = "3.0.7"
    const val SUPPORT_TEST = "1.0.2"
}

object BuildScriptLibs {
    const val ANDROID = "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
}

object Libs {
    const val APACHE_COMMONS = "org.apache.commons:commons-lang3:${Versions.APACHE_COMMONS}"
    const val COMPOSE_FOUNDATION = "androidx.compose.foundation:foundation:${Versions.COMPOSE}"
    const val COMPOSE_MATERIAL = "androidx.compose.material:material:${Versions.COMPOSE}"
    const val COMPOSE_RUNTIME = "androidx.compose.runtime:runtime-rxjava3:${Versions.COMPOSE}"
    const val COMPOSE_UI = "androidx.compose.ui:ui:${Versions.COMPOSE}"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
    const val GUAVA = "com.google.guava:guava:${Versions.GUAVA}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.KOTLIN}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val RX_JAVA_2 = "io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA_3}"
}

object PublishLibs {
    const val MAVEN_SETTINGS = "net.linguica.gradle:maven-settings-plugin:${Versions.MAVEN_SETTINGS}"
}

object SupportLibs {
    const val APP_COMPAT = "androidx.appcompat:appcompat:${Versions.ANDROID_X_APP_COMPAT}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.ANDROID_X_CONSTRAINT_LAYOUT}"
    const val DESIGN = "com.google.android.material:material:${Versions.ANDROID_X_MATERIAL}"
    const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:${Versions.ANDROID_X_RECYCLER_VIEW}"
    const val LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.ANDROID_X_LIFECYCLE}"
    const val VIEW_PAGER_2 = "androidx.viewpager2:viewpager2:${Versions.ANDROID_X_VIEW_PAGER2}"
}

object TestLibs {
    const val COMPOSE_UI_TEST = "androidx.compose.ui:ui-test-junit4:${Versions.COMPOSE}"
    const val COMPOSE_UI_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest:${Versions.COMPOSE}"
    const val CORE_KTX = "androidx.test:core-ktx:${Versions.ANDROID_X_CORE_CTX}"
    const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"
    const val ESPRESSO = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO}"
    const val ESPRESSO_CONTRIB = "androidx.test.espresso:espresso-contrib:${Versions.ESPRESSO}"
    const val ESPRESSO_INTENTS = "androidx.test.espresso:espresso-intents:${Versions.ESPRESSO}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val MOCKITO = "org.mockito:mockito-core:${Versions.MOCKITO}"
    const val MOCKITO_KOTLIN = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.MOCKITO_KOTLIN}"
    const val ROBOLECTRIC = "org.robolectric:robolectric:${Versions.ROBOLECTRIC}"
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
