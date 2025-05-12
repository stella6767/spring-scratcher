package freeapp.life.swaggerrestdoc.web

import freeapp.life.swaggerrestdoc.config.security.UserPrincipal
import freeapp.life.swaggerrestdoc.service.AuthService
import freeapp.life.swaggerrestdoc.web.dto.LoginReqDto
import freeapp.life.swaggerrestdoc.web.dto.SuccessResponse
import freeapp.life.swaggerrestdoc.web.dto.TokenDto
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RequestMapping("/auth")
@RestController
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginReqDto,
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
        return SuccessResponse (
            "load user by bearer token",
            authService.loadUser(principal)
        )
    }



}
