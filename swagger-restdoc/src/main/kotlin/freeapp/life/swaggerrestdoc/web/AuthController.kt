package freeapp.life.swaggerrestdoc.web

import freeapp.life.swaggerrestdoc.config.security.UserPrincipal
import freeapp.life.swaggerrestdoc.service.AuthService
import freeapp.life.swaggerrestdoc.web.dto.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerDto: RegisterDto
    ): SuccessResponse<UserResponseDto> {

        return SuccessResponse(
            "register successful",
            authService.register(registerDto)
        )
    }


    @PostMapping("/login")
    fun login(
        @Valid @RequestBody loginDto: LoginReqDto,
        response: HttpServletResponse,
    ): SuccessResponse<TokenDto> {

        return SuccessResponse(
            "login",
            authService.login(loginDto)
        )
    }

    @GetMapping("/load")
    fun loadUser(@AuthenticationPrincipal principal: UserPrincipal): SuccessResponse<*> {
        // 자동로그인 용도, 로그인상태 유지
        return SuccessResponse(
            "load user by bearer token",
            authService.loadUser(principal)
        )
    }


    @PatchMapping("/validate/password")
    fun validatePassword(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody passwordReqDto: PasswordReqDto,
    ): SuccessResponse<UserResponseDto> {

        val profile =
            authService.validatePassword(principal, passwordReqDto)

        return SuccessResponse(
            "validatePassword",
            profile
        )
    }


}
