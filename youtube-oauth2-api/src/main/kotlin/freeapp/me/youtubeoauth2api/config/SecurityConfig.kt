package freeapp.me.youtubeoauth2api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val configuration: AuthenticationConfiguration,
) {

    @Bean
    fun authenticationManager(): AuthenticationManager {
        return configuration.authenticationManager
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http.csrf { csrf -> csrf.disable() }
        http.formLogin { it.disable() }

        http.authorizeHttpRequests {
            it.requestMatchers("/").permitAll()
                .anyRequest().authenticated()
        }.oauth2Login {
            //Customizer.withDefaults<HttpSecurity>()
            it.userInfoEndpoint {
            }
            it.defaultSuccessUrl("/liked-videos")
        }.logout {
            it.logoutUrl("/logout")
        }

        return http.build()
    }
}
