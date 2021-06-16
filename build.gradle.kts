plugins {
    java
    application
    com.github.johnrengelman.shadow
    org.hidetake.ssh
}

group = "com.jacobcxdev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.ev3dev-lang-java:ev3dev-lang-java:2.6.2-SNAPSHOT")
    implementation("com.github.ev3dev-lang-java:lejos-navigation:0.2.0")
    implementation("io.socket:socket.io-client:2.0.0")
    implementation("io.socket:socket.io-server:3.0.1")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
}

application {
    mainClass.set("${group}.${project.name.toLowerCase()}.${project.name}")
}

tasks {
    shadowJar {
        archiveBaseName.set(project.name)
        mergeServiceFiles()
    }
}

apply(from = "remotes.gradle")
apply(from = "gradle/deploy.gradle")