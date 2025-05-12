package freeapp.life.swaggerrestdoc.util

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document
import com.epages.restdocs.apispec.ParameterDescriptorWithType
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.life.swaggerrestdoc.config.security.JwtTokenProvider
import freeapp.life.swaggerrestdoc.config.security.UserPrincipal
import freeapp.life.swaggerrestdoc.service.AuthService
import freeapp.life.swaggerrestdoc.service.TodoService
import freeapp.life.swaggerrestdoc.web.AuthController
import freeapp.life.swaggerrestdoc.web.TodoController

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.hypermedia.LinkDescriptor
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaField

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class, MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(
    *[
        TodoController::class,
        AuthController::class,
    ],
)
@Import(TestSecurityConfig::class)
open class ApiDocumentationBase {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    protected lateinit var todoService: TodoService

    @MockitoBean
    protected lateinit var authService: AuthService

    @MockitoBean
    protected lateinit var jwtTokenProvider: JwtTokenProvider


    private fun makeClaim(): Map<String, String> {
        return mapOf(
            "userId" to "1",
            "username" to "test",
            "status" to "ACTIVATED",
            "role" to "ADMIN",
            "email" to "admin@test.gmail",
            "authorities" to "ROLE_ADMIN",
        )
    }

    val fakePrincipal = UserPrincipal(makeClaim())




    protected fun documentApi(
        identifier: String,
        tag: String,
        summary: String,
        queryParams: Array<ParameterDescriptorWithType> = emptyArray(),
        requestFields: Array<FieldDescriptor> = emptyArray(),
        responseFields: Array<FieldDescriptor> = emptyArray(),
        links: Array<LinkDescriptor> = emptyArray(),
        requestSchema: Schema? = null,
        responseSchema: Schema? = null,
        pathParams: Array<ParameterDescriptor> = emptyArray(),
        requestHeaders: Array<HeaderDescriptor> = emptyArray(),
        //pathParams: PathParametersSnippet
    ): RestDocumentationResultHandler {
        return document(
            identifier,
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            resource(
                ResourceSnippetParameters.builder()
                    .tag(tag)
                    .summary(summary)
                    .apply {
                        if (queryParams.isNotEmpty()) {
                            queryParameters(*queryParams)
                        }
                        if (requestFields.isNotEmpty()) {
                            requestFields(*requestFields)
                        }
                        if (responseFields.isNotEmpty()) {
                            responseFields(*responseFields)
                        }
                        if (pathParams.isNotEmpty()) {
                            pathParameters(*pathParams)
                        }
                        if (requestHeaders.isNotEmpty()) {
                            requestHeaders(*requestHeaders)
                        }
                        if (links.isNotEmpty()) {
                            links(*links)
                        }
                        if (requestSchema != null) {
                            requestSchema(requestSchema)
                        }
                        if (responseSchema != null) {
                            responseSchema(responseSchema)
                        }
                    }
                    .build()
            )
        )
    }


    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider,

        ) {

        val springSecurity = SecurityMockMvcConfigurers.springSecurity()
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(restDocumentation)
            )
            .apply<DefaultMockMvcBuilder>(springSecurity)
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            //.alwaysDo<DefaultMockMvcBuilder>(write())
            .build()
    }

    protected fun authorizationHeader(): HeaderDescriptor =
        HeaderDocumentation.headerWithName("Authorization").description("Bearer 인증 토큰")

    protected fun commonResponseFields() = arrayOf(
        PayloadDocumentation.fieldWithPath("resultMsg").description("응답 메시지"),
        PayloadDocumentation.subsectionWithPath("data").description("응답 데이터")
    )


    protected fun getPageableResponseFields(
        prefix: String = "data",
    ): Array<FieldDescriptor> {
        return arrayOf(

            fieldWithPath("$prefix.pageable").description("페이지 정보"),
            fieldWithPath("$prefix.pageable.pageNumber").description("현재 페이지 번호"),
            fieldWithPath("$prefix.pageable.pageSize").description("페이지 크기"),
            fieldWithPath("$prefix.pageable.sort").description("정렬 정보"),
            fieldWithPath("$prefix.pageable.offset").description("오프셋"),
            fieldWithPath("$prefix.pageable.paged").description("페이징 사용 여부"),
            fieldWithPath("$prefix.pageable.unpaged").description("페이징 미사용 여부"),
            fieldWithPath("$prefix.totalElements").description("전체 요소 수"),
            fieldWithPath("$prefix.totalPages").description("전체 페이지 수"),
            fieldWithPath("$prefix.last").description("마지막 페이지 여부"),
            fieldWithPath("$prefix.size").description("페이지 크기"),
            fieldWithPath("$prefix.number").description("현재 페이지 번호"),
            fieldWithPath("$prefix.sort").description("정렬 정보"),
            fieldWithPath("$prefix.sort.empty").description("정렬 정보 존재 여부"),
            fieldWithPath("$prefix.sort.sorted").description("정렬됨 여부"),
            fieldWithPath("$prefix.sort.unsorted").description("정렬 안됨 여부"),
            fieldWithPath("$prefix.numberOfElements").description("현재 페이지의 요소 수"),
            fieldWithPath("$prefix.first").description("첫 페이지 여부"),
            fieldWithPath("$prefix.empty").description("결과 비어있음 여부")
        )
    }


    protected fun getPageableParameters(): Array<ParameterDescriptorWithType> {
        return arrayOf(
            parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
            parameterWithName("size").description("페이지 크기").optional()
        )
    }

    protected fun <T : Enum<T>> getEnumValues(enumType: Class<T>): String {
        return Arrays.stream(enumType.enumConstants)
            .map { type -> type.name }
            .collect(Collectors.joining(", "))
    }


    protected fun <T : Any> createRequestFieldsByInstance(instance: T): Array<FieldDescriptor> {
        return instance::class.memberProperties.map {
            val isMarkedNuallble =
                it.returnType.isMarkedNullable

            val description = if (it.returnType.isSubtypeOf(Enum::class.starProjectedType)) {
                //println("칮있디 요놈=?${it.name}")
                val enumClass = it.returnType.classifier as KClass<*>
                enumClass.java.enumConstants.toList().toString()
            } else {
                it.name
            }

            if (isMarkedNuallble) {
                fieldWithPath(it.name).description(description)
                    .type(it.javaField?.type ?: it.returnType).optional()
            } else if (it.returnType.isSubtypeOf(Map::class.starProjectedType)) {
                subsectionWithPath(it.name).description(it.name).type(Map::class.java)
            } else if (it.returnType.isSubtypeOf(List::class.starProjectedType)) {
                subsectionWithPath(it.name).description(it.name).type(List::class.java)
            } else {
                fieldWithPath(it.name).description(description)
                    .type(it.javaField?.type ?: it.returnType)
            }
        }.toTypedArray()
    }


}
