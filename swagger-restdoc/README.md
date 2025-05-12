
```
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.epages.restdocs-api-spec") version "0.19.4"
    id("org.hidetake.swagger.generator") version "2.19.2"


    kotlin("plugin.jpa") version "1.9.25"
}



extra["snippetsDir"] = file("build/generated-snippets")


openapi3 {

    val local = closureOf<Server> {
        url("http://localhost:8080")
        description("Local Development Server")
    } as Closure<Server>

    val dev = closureOf<Server> {
        url("https://dev-api.freeapp.me")
        description("dev Development Server")
    } as Closure<Server>

    setServers(listOf(local, dev))

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
	
       testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")
    testImplementation("org.instancio:instancio-junit:5.4.1")

    // Swagger UI
    swaggerUI("org.webjars:swagger-ui:5.21.0")


    

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
    options.set("docExpansion", "none")
    
}

```



[주소](https://github.com/stella6767/spring-scratcher/tree/main/swagger-restdoc)


# 참고

[Spring Rest Docs 와 Swagger-UI 연동](https://velog.io/@dainel/Spring-Rest-Docs-%EC%99%80-Swagger-UI-%EC%97%B0%EB%8F%99)

[[Spring] restdocs + swagger 같이 사용하기](https://velog.io/@hwsa1004/Spring-restdocs-swagger-%EA%B0%99%EC%9D%B4-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

[Swagger와 Spring rest docs, 두마리 토끼 잡기!](https://yummy0102.tistory.com/666)

[Spring - REST Docs 적용 및 최적화하기](https://backtony.tistory.com/37)

[Spring boot | Restdocs-api-spec with Swagger, Docker 완전 정복 하기](https://typo.tistory.com/entry/Spring-boot-Restdocs-api-spec-with-Swagger-Docker-%EC%99%84%EC%A0%84-%EC%A0%95%EB%B3%B5-%ED%95%98%EA%B8%B0)

[Spring Rest Docs로 행복해지는 간단한 방법
](https://blog.kmong.com/spring-rest-docs%EB%A1%9C-%EB%AA%A8%EB%91%90-%ED%96%89%EB%B3%B5%ED%95%B4%EC%A7%80%EB%8A%94-%EA%B0%84%EB%8B%A8%ED%95%9C-%EB%B0%A9%EB%B2%95-4ea80ccde1a0)

[Spring RestDocs 개선기(2) - 리플렉션을 이용한 Enum 문서 작성 자동화](https://sandcastle.tistory.com/120)

[OpenAPI Specification을 이용한 더욱 효과적인 API 문서화](https://tech.kakaopay.com/post/openapi-documentation/?ref=blog.cocoblue.me)

[]()

[]()

[]()

[]()
