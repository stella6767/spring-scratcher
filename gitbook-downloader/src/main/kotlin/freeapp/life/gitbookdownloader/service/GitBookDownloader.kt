package freeapp.life.gitbookdownloader.service

import com.github.slugify.Slugify
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap


@Service
class GitbookDownloader(
    private val restClient: RestClient,
    private val htmlParser: HtmlParser
) {
    private val visitedUrls = ConcurrentHashMap.newKeySet<String>()
    private val pages = ConcurrentHashMap<Int, PageContent>()
    private val contentHashes = ConcurrentHashMap<Int, String>()
    private val delay = 1000L // 1초 딜레이
    private val maxRetries = 3

    @Synchronized
    fun download(baseUrl: String): String {
        val status = DownloadStatus().apply {
            startTime = System.currentTimeMillis()
            status = "downloading"
        }

        try {
            // 초기 페이지 처리
            val initialContent = fetchWithRetry(baseUrl, status) ?: ""
            val navLinks = htmlParser.extractNavLinks(initialContent , baseUrl)
            status.totalPages = navLinks.size + 1

            // 메인 페이지 처리
            processPage(baseUrl, initialContent, 0, status)

            // 나머지 페이지 처리
            navLinks.forEachIndexed { index, url ->
                status.currentPage = index + 1
                status.currentUrl = url
                Thread.sleep(delay) // 요청 간 딜레이

                val content = fetchWithRetry(url, status)
                content?.let {
                    processPage(url, it, index + 1, status)
                }
            }

            return generateMarkdown()
        } catch (e: Exception) {
            status.status = "error"
            status.error = e.message
            throw e
        }
    }

    private fun processPage(url: String, html: String, index: Int, status: DownloadStatus) {
        if (url in visitedUrls) return
        htmlParser.parsePage(html, url)?.let { page ->
            val contentHash = page.content.hashCode()
            if (!contentHashes.containsValue(contentHash.toString())) {
                pages[index] = page.copy(index = index)
                status.pagesScraped.add(page.title)
                contentHashes[index] = contentHash.toString()
            }
            visitedUrls.add(url)
        }
    }

    private fun generateMarkdown(): String {
        val sb = StringBuilder("# Table of Contents\n")
        pages.values.sortedBy { it.index }.forEach { page ->
            sb.append("- [${page.title}](#${page.title.slugify()})\n")
        }
        sb.append("\n---\n")

        pages.values.sortedBy { it.index }.forEach { page ->
            sb.append("\n# ${page.title}\n\nSource: ${page.url}\n\n${page.content}\n---\n")
        }
        return sb.toString()
    }

    private fun fetchWithRetry(url: String, status: DownloadStatus): String? {
        var retries = 0
        var currentDelay = 2000L // 2초부터 시작

        while (retries < maxRetries) {
            try {
                val response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String::class.java)

                when (response.statusCode) {
                    HttpStatus.OK -> return response.body
                    HttpStatus.TOO_MANY_REQUESTS -> {
                        val retryAfter = response.headers["Retry-After"]?.toString()?.toIntOrNull() ?: 60
                        status.rateLimitReset = retryAfter
                        Thread.sleep(retryAfter * 1000L)
                    }
                    else -> return null
                }
            } catch (e: Exception) {
                if (retries++ < maxRetries) {
                    Thread.sleep(currentDelay)
                    currentDelay *= 2 // 지수 백오프
                } else {
                    throw e
                }
            }
        }
        return null
    }
}
