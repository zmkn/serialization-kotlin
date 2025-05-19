pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://repository.zmkn.com/repository/maven-public/")
        }
        google()
        mavenCentral()
    }
}
rootProject.name = "serialization-kotlin"
include(":jackson-kotlin")
project(":jackson-kotlin").name = "jackson-kotlin"
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
