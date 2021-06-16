rootProject.name = "EV3MazeProject"

buildscript {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    dependencies {
        classpath("com.github.johnrengelman:shadow:master-SNAPSHOT")
        classpath("org.hidetake:gradle-ssh-plugin:2.10.1")
    }
}
