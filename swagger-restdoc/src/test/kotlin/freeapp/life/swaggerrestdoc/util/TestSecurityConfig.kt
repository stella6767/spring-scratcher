package freeapp.life.swaggerrestdoc.util

import freeapp.life.swaggerrestdoc.entity.User
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@TestConfiguration
class TestSecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(*arrayOf("/auth/login", "/todo/**")).permitAll()
                    //.requestMatchers("/auth/**").hasAnyRole(User.Role.ADMIN.name,)
                    .anyRequest().authenticated()
            }

        return http.build()
    }

}
