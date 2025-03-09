plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
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

    // https://mvnrepository.com/artifact/org.pytorch/pytorch_java_only
    //implementation("org.pytorch:pytorch_java_only:2.1.0")

    implementation("ai.djl:api:0.32.0")
    implementation("ai.djl.pytorch:pytorch-engine:0.32.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testng:testng:7.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("ai.djl.pytorch:pytorch-native-auto:1.9.1")
// https://mvnrepository.com/artifact/ai.djl.pytorch/pytorch-jni
    implementation("ai.djl.pytorch:pytorch-jni:2.5.1-0.32.0")
// https://mvnrepository.com/artifact/ai.djl.pytorch/pytorch-native-cpu
    implementation("ai.djl.pytorch:pytorch-native-cpu:2.5.1:osx-aarch64")




    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
