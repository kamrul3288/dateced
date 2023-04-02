import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven ("https://oss.jfrog.org/libs-snapshot")
    maven ("https://jitpack.io")

}