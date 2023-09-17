plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.jleen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.freemarker:freemarker:2.3.32")
    implementation("com.twelvemonkeys.common:common-image:3.9.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}