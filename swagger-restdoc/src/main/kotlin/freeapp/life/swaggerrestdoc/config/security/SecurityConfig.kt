package freeapp.life.swaggerrestdoc.config.security


import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.life.swaggerrestdoc.entity.User
import freeapp.life.swaggerrestdoc.exception.GlobalExceptionHandler
import freeapp.life.swaggerrestdoc.web.dto.SuccessResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * Security config
 *
 * @property om
 * @property jwtTokenProvider
 * @property memberRepository
 * @constructor Create empty Security config
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
class SecurityConfig(
    private val configuration: AuthenticationConfiguration,
    private val mapper: ObjectMapper,
    private val jwtFilter: JwtFilter,
) {
    private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Value("\${security.debug:false}")
    private val isDebug = false

    @Value("\${spring.profiles.active:Unknown}")
    private lateinit var activeProfile: String

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    fun authenticationManager(): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            val arrays = arrayOf(
                AntPathRequestMatcher("/resources/*"),
                AntPathRequestMatcher("/static/*"),
                AntPathRequestMatcher("/img/*"),
                AntPathRequestMatcher("/js/*")
            )
            web.ignoring()
                .requestMatchers(*arrays)
        }
    }


    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf { csrf -> csrf.disable() }
            .cors {
                it.configurationSource(corsConfigurationSource())
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .headers { headersConfigurer ->
                headersConfigurer.frameOptions { it.sameOrigin() }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

        http
            .authorizeHttpRequests { authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers(*arrayOf("/auth/login", "/docs/**", "/todo/**")).permitAll()
                    .requestMatchers("/auth/**").hasAnyRole(User.Role.ADMIN.name)
                    .anyRequest().authenticated()
            }
            .logout {
                it.logoutUrl("/auth/logout")
                it.logoutSuccessHandler(CustomLogoutSuccessHandler(mapper))
                it.invalidateHttpSession(true)
                it.deleteCookies("JSESSIONID").permitAll()
            }    // 로그아웃은 기본설정으로 (/logout으로 인증해제)
            .exceptionHandling {
                it.accessDeniedHandler(JWTAccessDeniedHandler(mapper)) // 권한이 없는 사용자 접근 시
                it.authenticationEntryPoint(JWTAuthenticationEntryPoint(mapper)) //인증되지 않는 사용자 접근 시
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }


    class JWTAccessDeniedHandler(
        private val mapper: ObjectMapper,
    ) : AccessDeniedHandler {
        override fun handle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            accessDeniedException: AccessDeniedException
        ) {

            val httpStatus = HttpStatus.FORBIDDEN

            val problemDetail =
                GlobalExceptionHandler.createProblemDetail(
                    accessDeniedException,
                    httpStatus,
                    "Access Denied",
                    accessDeniedException.message ?: "Access Denied"
                )


            response.status = httpStatus.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"

            mapper.writeValue(response.writer, problemDetail)
        }
    }


    class JWTAuthenticationEntryPoint(
        private val mapper: ObjectMapper,
    ) : AuthenticationEntryPoint {
        override fun commence(
            request: HttpServletRequest, response: HttpServletResponse,
            authException: AuthenticationException
        ) {

            val httpStatus = HttpStatus.UNAUTHORIZED

            val problemDetail =
                GlobalExceptionHandler.createProblemDetail(
                    authException,
                    httpStatus,
                    "Authentication Failed",
                    authException.message ?: "Authentication failed"
                )


            response.status = httpStatus.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"

            mapper.writeValue(response.writer, problemDetail)
        }
    }

    class CustomLogoutSuccessHandler(
        private val mapper: ObjectMapper,
    ) : LogoutSuccessHandler {

        private val log = LoggerFactory.getLogger(this::class.java)
        override fun onLogoutSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication?
        ) {

            log.info("logout success")
            val context = SecurityContextHolder.getContext()
            context.authentication = null
            SecurityContextHolder.clearContext()
            val json = mapper.writeValueAsString(SuccessResponse("logout", Unit))
            mapper.writeValue(response.writer, json)
        }
    }


    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }


}
