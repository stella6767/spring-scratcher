package freeapp.life.gitbookdownloader

import freeapp.life.gitbookdownloader.service.GitbookDownloader
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor


@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class GitbookDownloaderApplicationTests(
    private val gitbookDownloader: GitbookDownloader
) {

    @Test
    fun contextLoads() {

        val download =
            gitbookDownloader.download("https://kotlin-jdsl.gitbook.io/docs")

        println(download)

    }

}
