import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.epages.restdocs-api-spec") version "0.19.0"
    id("org.hidetake.swagger.generator") version "2.19.2"


    kotlin("plugin.jpa") version "1.9.25"
}

group = "freeapp.life"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")


openapi3 {
    setServer("http://localhost:8080")
    title = "API 문서"
    description = "RestDocsWithSwagger Docs"
    version = "0.0.1"
    format = "yaml"
    //outputDirectory = "build/resources/main/static/docs"
}

swaggerSources {
    create("api") {
        setInputFile(layout.buildDirectory.file("api-spec/openapi3.yaml").get().asFile)
    }
}




dependencies {

    //json 직렬화
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-databind")


    //jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")


    //query
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.4")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.4")

    //sql log
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")


    //util
    implementation("org.apache.commons:commons-lang3:3.12.0")


    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.0")


    // Swagger UI
    swaggerUI ("org.webjars:swagger-ui:5.21.0")



    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

tasks.bootJar {
    dependsOn(tasks.getByName("generateSwaggerUIApi"))
    from("${tasks.getByName<GenerateSwaggerUI>("generateSwaggerUIApi").outputDir}") {
        into("static/docs/")
    }
}

tasks.withType<GenerateSwaggerUI> {
    dependsOn("openapi3")

    inputFile = layout.buildDirectory.file("api-spec/openapi3.yaml").get().asFile

    doFirst {

        val securitySchemesContent = """
            |  securitySchemes:
            |    APIKey:
            |      type: apiKey
            |      name: Authorization
            |      in: header
            |security:
            |  - APIKey: []  # Apply the security scheme here
        """.trimMargin()

        if (inputFile.exists()) {
            inputFile.appendText(securitySchemesContent)
        } else {
            throw GradleException("Input file ${inputFile.absolutePath} does not exist.")
        }
    }
}



tasks.register<Copy>("swaggerLocalCopy") {
    dependsOn("generateSwaggerUIApi")

    val sourcePath =
        "${tasks.getByName<GenerateSwaggerUI>("generateSwaggerUIApi").outputDir}"

    from(sourcePath)
    into(layout.projectDirectory.dir("src/main/resources/static/docs/"))
}
