package freeapp.me.youtubeoauth2api.config

import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository

//https://godekdls.github.io/Spring%20Security/oauth2/#oauth2authorizedclient


//class CustomOauth2Config {
//
//    fun clientRegistrationRepository(): ClientRegistrationRepository {
//        val clientRegistration = clientRegistration()
//        return InMemoryClientRegistrationRepository(clientRegistration)
//    }
//
//    private fun clientRegistration(): ClientRegistration {
//        return CommonOAuth2Provider
//            .GOOGLE
//            .getBuilder("google")
//            .clientId(clientId)
//            .clientSecret(clientSecret)
//            .build()
//    }
//}
