package freeapp.life.gitbookdownloader.service

import org.apache.commons.text.StringEscapeUtils

data class DownloadStatus(
    var totalPages: Int = 0,
    var currentPage: Int = 0,
    var currentUrl: String = "",
    var status: String = "idle",
    var error: String? = null,
    var startTime: Long? = null,
    var pagesScraped: MutableList<String> = mutableListOf(),
    var outputFile: String? = null,
    var rateLimitReset: Int? = null
) {
    fun elapsedTime(): Double {
        return startTime?.let { (System.currentTimeMillis() - it) / 1000.0 } ?: 0.0
    }
}


data class PageContent(
    val title: String,
    val content: String,
    val url: String,
    val index: Int = 0
)


fun String.slugify(): String {
    return this
        .lowercase()
        .replace("\\s+".toRegex(), "-")    // 공백을 하이픈으로
        .replace("[^a-z0-9-_.]".toRegex(), "") // 영문/숫자/하이픈/언더스코어만 허용
        .replace("-+".toRegex(), "-")      // 연속 하이픈 제거
        .removePrefix("-")
        .removeSuffix("-")
}

