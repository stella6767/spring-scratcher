package freeapp.me.youtubeoauth2api.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import freeapp.me.youtubeoauth2api.dto.YoutubeItemSnippetDto
import freeapp.me.youtubeoauth2api.dto.YoutubeResponseDto
import org.springframework.stereotype.Service


@Service
class YoutubeService(
    private val mapper: ObjectMapper,
) {


    fun getLikedVideos(accessToken: String): List<String> {

        val credential =
            GoogleCredential().setAccessToken(accessToken)

        val youtube = YouTube.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).build()

        val likedVideos =
            mutableListOf<String>()
        var nextPageToken: String = ""


        do {
            val request = youtube.playlistItems()
                .list("snippet")
                .apply {
                    playlistId = "LL" // '좋아요' 플레이리스트 고정 ID
                    maxResults = 50
                    pageToken = nextPageToken
                }

            val response = request.execute()
            response.items?.forEach { item ->
                item.snippet?.title?.let { likedVideos.add(it) }
            }
            println(response)
            nextPageToken = response?.nextPageToken ?: ""

        } while (nextPageToken.isNotEmpty())


        return likedVideos
    }


    fun getLikedVideosByChunk(accessToken: String, callback: (YoutubeResponseDto)-> Unit) {

        val credential =
            GoogleCredential().setAccessToken(accessToken)

        val youtube = YouTube.Builder(
            NetHttpTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).build()

        var nextPageToken: String = ""

        do {
            val request = youtube.playlistItems()
                .list("snippet")
                .apply {
                    playlistId = "LL" // '좋아요' 플레이리스트 고정 ID
                    maxResults = 50
                    pageToken = nextPageToken
                }

            val response = request.execute()
            response.items?.forEach { item ->
                val readValue = mapper.readValue(item.snippet.toString(), YoutubeItemSnippetDto::class.java)
                val responseDto =
                    readValue.toYoutubeResponseDto()

                val keywords = listOf("아침", "루틴", "your", "girlfriend", "routine",)

                if (keywords.any { responseDto.title!!.contains(it) }){
                    println(responseDto.title)
                }

                if((responseDto.title == "Deleted video") || (responseDto.title!!.contains("Private"))) {
                    println(readValue)
                    println(item.snippet?.resourceId?.videoId ?: "")

                }
                callback(responseDto)
            }
            nextPageToken = response?.nextPageToken ?: ""
            //여기서 청크 단위로 계속 리턴해서 브라우저에서 보는 걸 원해!!
        } while (nextPageToken.isNotEmpty())
    }


}
