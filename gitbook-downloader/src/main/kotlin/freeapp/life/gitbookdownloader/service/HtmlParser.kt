package freeapp.life.gitbookdownloader.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component

@Component
class HtmlParser {
    fun extractNavLinks(html: String, baseUrl: String): List<String> {
        return try {
            val doc = Jsoup.parse(html, baseUrl)
            val links = mutableSetOf<String>()

            // GitBook 네비게이션 구조 파싱
            doc.select("nav a[href], aside a[href], .nav-links a[href]").forEach { element ->
                element.absUrl("href").takeIf { it.isNotBlank() }?.let { links.add(it) }
            }

            links.filter { it.startsWith(baseUrl) && !it.contains("#") }.distinct()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parsePage(html: String, url: String): PageContent? {
        return try {
            val doc = Jsoup.parse(html, url)
            val title = extractTitle(doc)
            val content = extractMainContent(doc)

            PageContent(
                title = title,
                content = cleanMarkdown(content),
                url = url
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun extractTitle(doc: Document): String {
        return doc.selectFirst("h1")?.text()?.trim()
            ?: doc.title().split("[|\\-–]".toRegex()).first().trim()
    }

    private fun extractMainContent(doc: Document): String {
        doc.select("nav, aside, header, footer, script, style").remove()
        return doc.selectFirst("main, article, .markdown, .content")?.html() ?: ""
    }

    private fun cleanMarkdown(html: String): String {
        // 간단한 마크다운 변환 예시 (실제 구현 시 라이브러리 사용 권장)
        return html.replace("<h1>".toRegex(), "# ")
            .replace("<h2>".toRegex(), "## ")
            .replace("<p>".toRegex(), "\n")
            .replace("<[^>]+>".toRegex(), "")
            .replace("\\n{3,}".toRegex(), "\n\n")
    }
}

