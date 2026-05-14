
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(17)

    jvm()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
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
        version    = "2.1.0",
    )

    pom {
        name.set("DateCed")
        description.set(
            "A Kotlin Multiplatform date/time library inspired by PHP Carbon. " +
                    "Supports Android, JVM, iOS, and more."
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
