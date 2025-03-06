package freeapp.me.youtubeoauth2api.controller

import freeapp.me.youtubeoauth2api.service.YoutubeService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.util.concurrent.Executors


@Controller
class YoutubeController(
    private val youtubeService: YoutubeService,
    private val authorizedClientService: OAuth2AuthorizedClientService,
) {


    @GetMapping("/")
    fun hello(): String {
        return "page/index"
    }


    @GetMapping("/liked-videos2")
    @ResponseBody
    fun getLikedVideosByChunk(@AuthenticationPrincipal oauth2User: OAuth2User): ResponseBodyEmitter {
        val emitter = ResponseBodyEmitter()
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val authorizedClient =
                    authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>("google", oauth2User.name)
                val accessToken =
                    authorizedClient.accessToken.tokenValue
                youtubeService.getLikedVideosByChunk(accessToken) { video ->
                    emitter.send(video) // 각 영상을 개별적으로 전송
                }
                emitter.complete()
            } catch (e: Exception) {
                emitter.completeWithError(e)
            }
        }
        return emitter
    }

    @GetMapping("/liked-videos")
    @ResponseBody
    fun getLikedVideos(@AuthenticationPrincipal oauth2User: OAuth2User): List<String> {

        val authorizedClient =
            authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>("google", oauth2User.name)

        val accessToken =
            authorizedClient.accessToken
        println(accessToken.tokenValue)

        return youtubeService.getLikedVideos(accessToken.tokenValue)
    }


}
