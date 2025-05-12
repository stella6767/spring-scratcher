plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("gg.jte.gradle") version "3.1.16"
}

group = "freeapp.me"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // https://mvnrepository.com/artifact/com.google.zxing
    implementation("com.google.zxing:javase:3.5.3")
    implementation("com.google.zxing:core:3.5.3")

    implementation(platform("software.amazon.awssdk:bom:2.20.56"))
    implementation("software.amazon.awssdk:s3")


    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("gg.jte:jte:3.1.16")
    implementation("gg.jte:jte-spring-boot-starter-3:3.1.16")
    implementation("io.github.wimdeblauwe:htmx-spring-boot:4.0.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

jte {
    generate()
    binaryStaticContent = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}
