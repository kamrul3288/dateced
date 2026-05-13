
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}


kotlin {
    // Set the JVM toolchain to Java 17 for both Android and JVM targets
    // So that we don't need targetCompatibility and sourceCompatibility in Android block
    jvmToolchain(17)

    android {
        namespace = "com.iamkamrul.dateced"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withHostTest {
            // Enable unit tests
        }
    }

    // This library not only supports Android but also JVM(ex: desktop, server, etc.)
    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.runtime)
            implementation(libs.androidx.compose.ui)
        }
        // Compose compiler plugin applies to all targets; JVM needs the runtime on its classpath too
        jvmMain.dependencies {
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.runtime)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    "coreLibraryDesugaring"(libs.desugar)
}
// ======================================================================
// Maven Central Publishing Configuration
// ======================================================================
mavenPublishing {

    publishToMavenCentral()
    signAllPublications()

    coordinates(
        groupId    = "io.github.kamrul3288",
        artifactId = "dateced",
        version    = "2.0.0",
    )

    pom {
        name.set("DateCed")
        description.set(
            "A Kotlin Multiplatform date/time library inspired by PHP Carbon. " +
                    "Supports Android (Compose + XML), JVM, and more."
        )
        inceptionYear.set("2024")
        url.set("https://github.com/kamrul3288/dateced")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("KamrulHasan")
                name.set("Kamrul Hasan")
                email.set("kamrulhasan3288@gmail.com")
                url.set("https://github.com/kamrul3288")
            }
        }

        scm {
            url.set("https://github.com/kamrul3288/dateced")
            connection.set("scm:git:git://github.com/kamrul3288/dateced.git")
            developerConnection.set("scm:git:ssh://git@github.com/kamrul3288/dateced.git")
        }
    }
}