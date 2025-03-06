package freeapp.me.youtubeoauth2api.dto

data class YoutubeItemSnippetDto(
    val channelId: String?,
    val channelTitle: String?,
    val description: String?,
    val playlistId: String?,
    val position: Int,
    val publishedAt: String?,
    val title: String?,
    val videoOwnerChannelId: String?,
    val videoOwnerChannelTitle: String?
) {

    fun toYoutubeResponseDto(): YoutubeResponseDto {

        return YoutubeResponseDto(
            this.videoOwnerChannelTitle,
            this.title
        )
    }

}


data class YoutubeResponseDto(
    val videoOwnerChannelTitle: String?,
    val title: String?,
)
