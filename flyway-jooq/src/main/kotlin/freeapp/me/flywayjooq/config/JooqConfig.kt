package freeapp.me.flywayjooq.config

import org.jooq.impl.DefaultConfiguration
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class JooqConfig(

) {

    @Bean
    fun jooqDefaultConfigurationCustomizer(): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer { c: DefaultConfiguration ->
            // 쿼리에서 스키마 이름을 생략
            // 테스트 환경과 운영 환경에서 DB 스키마가 다를 경우, 스키마 생략하면 더 유연하게 대응 가능.
            c.settings().withRenderSchema(false)
        }
    }

}
