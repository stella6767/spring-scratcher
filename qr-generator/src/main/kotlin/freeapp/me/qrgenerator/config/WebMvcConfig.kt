package freeapp.me.qrgenerator.config

import com.fasterxml.jackson.databind.ObjectMapper

import org.springframework.beans.factory.annotation.Value

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebMvcConfig (
    private val mapper: ObjectMapper
) : WebMvcConfigurer {

    @Value("\${spring.profiles.active:unknown}")
    private val profile: String? = null

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .exposedHeaders("HX-Push")
    }

}
