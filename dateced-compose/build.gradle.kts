
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(17)

    android {
        namespace = "com.iamkamrul.dateced.compose"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":dateced"))
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.runtime)
            implementation(libs.androidx.compose.ui)
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
        artifactId = "dateced-compose",
        version    = "2.2.0",
    )

    pom {
        name.set("DateCed Compose")
        description.set(
            "Jetpack Compose extensions for DateCed — rememberCurrentTime(), " +
                    "rememberFromNow(), rememberTimeDifference()."
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
