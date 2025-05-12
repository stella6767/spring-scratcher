package freeapp.life.swaggerrestdoc.web

import freeapp.life.swaggerrestdoc.util.ApiDocumentationBase
import freeapp.life.swaggerrestdoc.web.dto.*
import org.instancio.Instancio
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerTest : ApiDocumentationBase() {

    @Test
    fun login() {
        // Given
        val uri = "/auth/login"

        val loginDto = Instancio.of(LoginReqDto::class.java)
            .create()

        val tokenDto = Instancio.of(TokenDto::class.java)
            .create()

        Mockito
            .`when`(authService.login(loginDto))
            .thenReturn(tokenDto)

        // JSON 문자열로 변환
        val content = objectMapper.writeValueAsString(loginDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
        )
            .andExpect(status().isOk)
            .andDo(

                documentApi(
                    identifier = "auth-login",
                    tag = "Auth API",
                    summary = "로그인 API",
                    requestFields = arrayOf(
                        fieldWithPath("email").description("로그인 이메일"),
                        fieldWithPath("password").description("로그인 비밀번호")
                    ),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.accessToken").description("JWT 인증 토큰")
                    ),
                )
            )
    }

    @Test
    fun loadUser() {
        // Given
        val uri = "/auth/load"

        val userResponseDto = Instancio.of(UserResponseDto::class.java)
            .create()

        Mockito
            .`when`(authService.loadUser(fakePrincipal))
            .thenReturn(userResponseDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
                .header("Authorization", BearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.user(fakePrincipal))
        )
            .andExpect(status().isOk)
            .andDo(
                documentApi(
                    identifier = "auth-load-user",
                    tag = "Auth API",
                    summary = "사용자 정보 불러오기 API",
                    // 요청 헤더는 documentApi에서 지원하지 않는 것 같습니다
                    // 이 부분은 추가 구현이 필요합니다
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("사용자 ID"),
                        fieldWithPath("data.email").description("사용자 이메일"),
                        fieldWithPath("data.username").description("사용자 이름"),
                        fieldWithPath("data.role").description("사용자 역할"),
                        fieldWithPath("data.createdAt").description("계정 생성일"),
                        fieldWithPath("data.updatedAt").description("계정 수정일")
                    ),
                    requestHeaders = arrayOf(authorizationHeader())
                )

            )
    }

    @Test
    fun validatePassword() {
        // Given
        val uri = "/auth/validate/password"

        val passwordReqDto = Instancio.of(PasswordReqDto::class.java)
            .create()

        val userResponseDto = Instancio.of(UserResponseDto::class.java)
            .create()

        Mockito
            .`when`(authService.validatePassword(fakePrincipal, passwordReqDto))
            .thenReturn(userResponseDto)

        // JSON 문자열로 변환
        val content = objectMapper.writeValueAsString(passwordReqDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.patch(uri)
                .header("Authorization", BearerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
                .with(SecurityMockMvcRequestPostProcessors.user(fakePrincipal))
        )
            .andExpect(status().isOk)
            .andDo(
                documentApi(
                    identifier = "auth-validate-password",
                    tag = "Auth API",
                    summary = "비밀번호 검증 API",
                    requestHeaders = arrayOf(
                        authorizationHeader(),
                    ),
                    requestFields = arrayOf(
                        fieldWithPath("password").description("검증할 비밀번호")
                    ),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("사용자 ID"),
                        fieldWithPath("data.email").description("사용자 이메일"),
                        fieldWithPath("data.username").description("사용자 이름"),
                        fieldWithPath("data.role").description("사용자 역할"),
                        fieldWithPath("data.createdAt").description("계정 생성일"),
                        fieldWithPath("data.updatedAt").description("계정 수정일")
                    )
                )

            )
    }

    @Test
    fun register() {
        // Given
        val uri = "/auth/register"

        val registerDto = RegisterDto(
            email = "test@test.com",
            password = "test",
            username = "test",
        )

        val userResponseDto = Instancio.of(UserResponseDto::class.java)
            .create()

        Mockito
            .`when`(authService.register(registerDto))
            .thenReturn(userResponseDto)

        // JSON 문자열로 변환
        val content = objectMapper.writeValueAsString(registerDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
        )
            .andExpect(status().isOk)
            .andDo(
                documentApi(
                    identifier = "auth-register",
                    tag = "Auth API",
                    summary = "회원가입 API",
                    requestFields = arrayOf(
                        fieldWithPath("email").description("사용자 이메일"),
                        fieldWithPath("password").description("사용자 비밀번호"),
                        fieldWithPath("username").description("사용자 이름"),
                        // RegisterDto에 추가 필드가 있다면 여기에 추가
                    ),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("생성된 사용자 ID"),
                        fieldWithPath("data.email").description("사용자 이메일"),
                        fieldWithPath("data.username").description("사용자 이름"),
                        fieldWithPath("data.role").description("사용자 역할"),
                        fieldWithPath("data.createdAt").description("계정 생성일"),
                        fieldWithPath("data.updatedAt").description("계정 수정일")
                    )
                )
            )
    }

}
