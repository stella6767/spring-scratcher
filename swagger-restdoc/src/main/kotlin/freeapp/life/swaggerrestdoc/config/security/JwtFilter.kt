package freeapp.life.swaggerrestdoc.config.security

import freeapp.life.swaggerrestdoc.util.TokenUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtFilter(
    private val tokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {
    // 실제 필터링 로직은 doFilterInternal 에 들어감
    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val jwt = TokenUtil.resolveToken(request)
        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

            val authentication: Authentication =
                tokenProvider.getAuthentication(jwt ?: "")
            //인증
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

}
