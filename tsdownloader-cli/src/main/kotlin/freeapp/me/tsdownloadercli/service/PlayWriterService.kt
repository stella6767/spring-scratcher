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
            //.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        )

        val page = context.newPage()

        // ✅ 특정 요청을 차단하여 브라우저가 가져오지 않도록 함
//        page.route("**/*.m3u8") { route ->
//            println("🚫 m3u8 요청 차단됨: ${route.request().url()}")
//            m3u8FilesList.add(route.request().url()) // URL만 저장
//            //route.abort() // 요청 중단 (다운로드 방지)
//            route.resume()
//        }

//        page.route("**/*") { route ->
//            val request = route.request()
//
//            println(request.url())
//
////            // 리다이렉트 비활성화 옵션
////            val fetchOptions = Route.FetchOptions().apply {
////                maxRedirects = 0
////            }
////            // 수정된 요청으로 fetch 실행
////            val response = route.fetch(fetchOptions)
////
////            // 리다이렉트 응답 처리
////            if (response.status() >= 300 && response.status() < 400) {
////                val redirectUrl = response.headers()["location"]
////                println("차단된 리다이렉트: $redirectUrl")
////            }
////            //response
////            route.fulfill()
//            route.resume()
//        }


        // 1. 라우팅 핸들러 설정 (모든 요청 가로챔)
//        page.route("**/*") { route ->
//            val request = route.request()
//            val url = request.url()
//            println(url)
//            // 2. m3u8 요청만 필터링
//            if (url.contains("m3u8")) {
//                println("✅ 캡처 완료: $url")
//                route.abort() // 3. 실제 요청 중단
//            } else {
//                route.resume() // 4. 다른 리소스는 정상 진행
//            }
//        }

//        page.onResponse { response ->
//            println(response.status())
//            println(response.allHeaders())
//        }


        page.onRequest { request ->
            val url = request.url()
            println(">> Request: ${request.url()} ${request.resourceType()}")
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

////        page.onResponse { response ->
////            println("<< Response: ${response.url()} ${response.status()}")
////        }
//        page.onRequestFailed { request ->
//            println("!! Failed: ${request.url()} ${request.failure()}")
//        }

//        page.onFrameDetached { req ->
//            println("Sub frame: ${req.url()}") // iframe 내부 요청 캡처
//        }


        // 5. 페이지 네비게이션 실행
        try {
            page.navigate(url)
            page.waitForLoadState(LoadState.LOAD)

        } catch (e: PlaywrightException) {
            // 6. 의도된 abort는 예외로 처리되지 않음
            println("네비게이션 완료")
        }
        // 네트워크 활동이 완료될 때까지 대기


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
