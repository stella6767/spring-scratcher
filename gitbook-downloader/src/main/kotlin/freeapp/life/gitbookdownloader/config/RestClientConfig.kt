package freeapp.life.gitbookdownloader.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.io.IOException

@Configuration
class RestClientConfig {

    @Bean
    fun restClient(): RestClient {
        return RestClient.builder()
            .requestFactory(JdkClientHttpRequestFactory())
            .defaultHeaders { headers ->
                headers.contentType = MediaType.TEXT_HTML
                headers.accept = listOf(MediaType.TEXT_HTML)
            }
            .requestInterceptor(RetryableRestClient())
            .build()
    }

    class RetryableRestClient : ClientHttpRequestInterceptor {
        override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            execution: ClientHttpRequestExecution
        ): ClientHttpResponse {
            var retries = 0
            while (retries < 3) {
                try {
                    return execution.execute(request, body)
                } catch (e: IOException) {
                    if (retries++ == 2) throw e
                    Thread.sleep(2000L * retries)
                }
            }
            throw IllegalStateException("Max retries exceeded")
        }

    }

}
