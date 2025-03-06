import org.flywaydb.gradle.FlywayExtension
import org.gradle.internal.impldep.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.jooq.meta.jaxb.ForcedType

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"

    id("org.flywaydb.flyway") version "9.22.3"
    id("nu.studer.jooq") version "9.0" // JOOQ 코드 생성 플러그인
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

    implementation("org.springframework.boot:spring-boot-starter-jooq")
    jooqGenerator("com.mysql:mysql-connector-j") // JOOQ 코드 생성을 위한 MySQL 드라이버
    jooqGenerator("org.jooq:jooq-meta:3.19.14")
    jooqGenerator("org.jooq:jooq-codegen:3.19.14")


    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val dbUser: String = System.getProperty("db-user") ?: "root"
val dbPassword: String = System.getProperty("db-passwd") ?: "1234"

jooq {
    configurations {
        create("todoDB") {
            generateSchemaSourceOnCompilation.set(false) // 빌드할 때 자동으로 생성
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3306/todo"
                    user = dbUser
                    password = dbPassword
                }


                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator" // 코틀린 제너레이터 명시
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "todo"
                        //forcedTypes.onEach {}
//                        forcedTypes.add(ForcedType().withUserType("java.lang.Long").withIncludeTypes("int unsigned"))
//                        forcedTypes.add(
//                            ForcedType().withUserType("java.lang.Long")
//                                .withIncludeTypes("INTEGER") // ✅ 대소문자 무시
//                        )
//                        forcedTypes.add(ForcedType().withUserType("java.lang.Long").withIncludeTypes("bigint"))
//                        forcedTypes.add(
//                            ForcedType().withUserType("java.lang.Integer").withIncludeTypes("tinyint unsigned")
//                        )
//                        forcedTypes.add(
//                            ForcedType().withUserType("java.lang.Integer").withIncludeTypes("smallint unsigned")
//                        )
                    }
                    generate.apply {
                        //isDaos = true
                        //isJavaTimeTypes = true
                        isRecords = true
                        isFluentSetters = true
                        isDeprecated = false
                        isPojosAsKotlinDataClasses = true
                        isKotlinSetterJvmNameAnnotationsOnIsPrefix = true
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}

configure<FlywayExtension> {
    url = "jdbc:mysql://localhost:3306/todo"
    user = dbUser
    password = dbPassword
    locations = arrayOf("filesystem:src/main/resources/db/migration/common","filesystem:src/main/resources/db/migration/local")

    // Repair에 필요한 추가 옵션
    //repair() // Flyway 9.x 이상에서 명시적 호출
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}


sourceSets {
    main {
        kotlin {
            srcDirs(listOf("src/main/kotlin", "build/generated-src/jooq/main"))
            // 생성된 코드를 프로젝트 소스로 인식하도로 설정
        }
    }
}

// JOOQ 코드 생성이 컴파일 전에 실행되도록 설정
//tasks.compileJava {
//    dependsOn(tasks.withType(JooqGenerate::class.java))
//}

//tasks.named('compileTestJava').configure {
//    it.dependsOn(tasks.named('generateJooq'))
//}
