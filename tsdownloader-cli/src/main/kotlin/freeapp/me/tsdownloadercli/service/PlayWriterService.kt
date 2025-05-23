package freeapp.me.tsdownloadercli.service

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.microsoft.playwright.*
import com.microsoft.playwright.options.HarMode
import com.microsoft.playwright.options.LoadState
import freeapp.me.tsdownloadercli.util.HttpReqHeaderDto
import freeapp.me.tsdownloadercli.util.M3u8RequestDto
import freeapp.me.tsdownloadercli.util.PlaywrightStealth
import mu.KotlinLogging
import java.nio.file.Paths


class PlayWriterService(

) {

    private val log = KotlinLogging.logger { }
    private val gson = Gson()
    fun retrieveM3U8requestFilesByHarFile(harFilePath: String): MutableList<M3u8RequestDto> {

        //todo path 전처리

        val harPath =
            Paths.get("/Users/stella6767/IdeaProjects/scratcher/tsdownloader-cli/caputre.har")

        val jsonText = harPath.toFile().readText()
        val harJson = JsonParser.parseString(jsonText).asJsonObject
        val entries =
            harJson.getAsJsonObject("log").getAsJsonArray("entries")

        val m3u8RequestDtos:MutableList<M3u8RequestDto> = mutableListOf()

        for (entry in entries) {
            val request =
                entry.asJsonObject.getAsJsonObject("request")
            val url = request.get("url").asString
            if (url.contains("m3u8")) {
                val requestHeaders =
                    request.get("headers").asJsonArray

                val headerDtos =
                    gson.fromJson(requestHeaders, Array<HttpReqHeaderDto>::class.java).toList()

                val requestDto =
                    M3u8RequestDto(reqHeaders = headerDtos, m3u8file = url)
                m3u8RequestDtos.add(requestDto)
            }
        }

        return m3u8RequestDtos
    }



    /**
     * Retrieve m3u8request files
     *
     * m3u8 network request 를 캡처하지 못하는 문제.. 이유를 알 수 없다. 일단 폐기처분.
     *
     * @param url
     * @return
     */

    fun retrieveM3U8requestFiles(url: String): List<String> {

        //val latch = CountDownLatch(1)

        val m3u8FilesList = mutableListOf<String>()
        val playwright = Playwright.create()

        val browser =
            playwright.chromium().launch(
                BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setArgs(
                        listOf(
                            "--disable-blink-features=AutomationControlled",
                            "--exclude-switches=enable-automation"
                        )
                    ) //자동화 탐지 회피
            )
        val context = browser.newContext(
            Browser.NewContextOptions()
                //.setStorageState(null) //캐시 비활성화.
                //.setServiceWorkers(ServiceWorkerPolicy.BLOCK)
                .setIgnoreHTTPSErrors(true) // 3. HTTPS 오류 무시
                //.setBypassCSP(true)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            //.setProxy(Proxy("0.0.0.0:60614"))
        )


        val page = context.newPage()
        PlaywrightStealth.applyStealth(page)

        val webdriverFlag = page.evaluate("() => window.navigator.webdriver")
        println("window navigator webdriver value: $webdriverFlag") // 예상 출력: null

        page.onRequest { request ->
            val url = request.url()
            if (request.resourceType() == "xhr" || request.resourceType() == "fetch") {
                println(">> Request: ${request.url()} ${request.resourceType()}")
            }
        }

        // 5. 페이지 네비게이션 실행
        try {
            page.navigate(url)
            Thread.sleep(100000)
            page.screenshot(Page.ScreenshotOptions().setPath(Paths.get("example2.png")))
            //page.waitForLoadState(LoadState.NETWORKIDLE) // 네트워크 활동이 완료될 때까지 대기
        } catch (e: PlaywrightException) {
            println("네비게이션 완료")
        }

        context.close()
        browser.close()
        playwright.close()

        return m3u8FilesList
    }





    fun makeHarFileViaUrl(url: String) {

        val playwright = Playwright.create()

        val browser = playwright.chromium().launch(
            BrowserType.LaunchOptions().setHeadless(false) // 브라우저 창 띄우기
        )

        // 브라우저 컨텍스트 생성 (HAR 파일 기록)
        val harPath = Paths.get("network_capture.har")
        println(harPath.toAbsolutePath())

        val context = browser.newContext(
            Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                //.setServiceWorkers(ServiceWorkerPolicy.ALLOW)
                .setRecordHarPath(harPath)
                .setRecordHarMode(HarMode.MINIMAL)
                //https://proxycompass.com/ko/free-proxy/
                //.setProxy(Proxy("http://8.221.141.88:3129"))
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
        )


        val page = context.newPage()
        page.navigate(url) // 원하는 웹사이트 접속

        page.waitForLoadState(LoadState.NETWORKIDLE) // 모든 네트워크 요청을 캡처할 수 있도록 대기

        val jsonText = harPath.toFile().readText()

        val harJson = JsonParser.parseString(jsonText).asJsonObject
        val entries =
            harJson.getAsJsonObject("log").getAsJsonArray("entries")

        for (entry in entries) {

            val request =
                entry.asJsonObject.getAsJsonObject("request")
            val url = request.get("url").asString

            if (url.contains("mp4") || url.contains("m3u8")) {
                println(url)
            }
        }

        // 브라우저 종료 및 HAR 저장
        context.close()
        browser.close()
        playwright.close()
    }


}
