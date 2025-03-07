package freeapp.me.tsdownloadercli.service

import com.google.gson.JsonParser
import com.microsoft.playwright.*
import com.microsoft.playwright.options.HarMode
import com.microsoft.playwright.options.LoadState
import mu.KotlinLogging
import java.nio.file.Paths


class PlayWriterService(

) {

    private val log = KotlinLogging.logger { }


    fun retrieveM3U8requestFiles(url: String): List<String> {

        //val latch = CountDownLatch(1)

        val m3u8FilesList = mutableListOf<String>()
        val playwright = Playwright.create()

        val browser =
            playwright.chromium().launch(
                BrowserType.LaunchOptions()
                    .setHeadless(false)
                //.setArgs(listOf("--disable-features=DownloadsBlockSubframe"))// 자동 다운로드 방지
            )

        val context = browser.newContext(
            Browser.NewContextOptions()
                .setStorageState(null) //캐시 비활성화.
                //.setServiceWorkers(ServiceWorkerPolicy.BLOCK)
                //.setIgnoreHTTPSErrors(true) // 3. HTTPS 오류 무시
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        )

        val page = context.newPage()

        page.addInitScript(
            """
                Object.defineProperty(navigator, 'webdriver', {
                    get: () => undefined
                });
                window.chrome = { runtime: {} };
                """
        )



        page.onRequest { request ->
            val url = request.url()
            if (request.resourceType() == "xhr" || request.resourceType() == "fetch") {
                println(">> Request: ${request.url()} ${request.resourceType()}")
            }


//            if (url.contains("m3u8")) {
//                println("🔗 동영상 URL 감지됨: $url")
//                m3u8FilesList.add(url)
//                val headers = request.headers()
//                val method = request.method()
//                val postData = request.postData()
//                latch.countDown()  // URL을 찾았을 때 래치 해제
//            }
//            if (request.resourceType() == "xhr") {
//                println("XHR 요청 감지됨: ${request.url()}")
//            }
        }

        page.onFrameDetached { req ->
            println("Sub frame: ${req.url()}") // iframe 내부 요청 캡처
        }


        // 5. 페이지 네비게이션 실행
        try {
            page.navigate(url)
            page.waitForLoadState(LoadState.NETWORKIDLE) // 네트워크 활동이 완료될 때까지 대기
        } catch (e: PlaywrightException) {
            println("네비게이션 완료")
        }


//        val found =
//            latch.await(10, TimeUnit.SECONDS) //찾을 때까지 대기
//
//        if (found) {
//            log.info("Target URL encountered, proceeding with further steps")
//        } else {
//            log.info("Timeout reached, target URL not encountered")
//        }

//        Thread.sleep(10000)

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
