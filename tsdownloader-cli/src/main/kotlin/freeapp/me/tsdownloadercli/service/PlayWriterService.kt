package freeapp.me.tsdownloadercli.service

import com.microsoft.playwright.*
import com.microsoft.playwright.options.HarMode
import com.microsoft.playwright.options.LoadState
import com.microsoft.playwright.options.ServiceWorkerPolicy
import com.microsoft.playwright.options.WaitForSelectorState
import mu.KotlinLogging
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PlayWriterService(

) {

    private val log = KotlinLogging.logger {  }

    fun retrieveM3U8requestFiles(url:String): List<String> {

        //val latch = CountDownLatch(1)

        val m3u8FilesList = mutableListOf<String>()

        val playwright = Playwright.create()

        val browser =
            playwright.chromium().launch(BrowserType.LaunchOptions()
                .setHeadless(false)
                .setArgs(listOf("--autoplay-policy=no-user-gesture-required")) // 2. 자동 재생 정책 변경
            )


        val harPath = Paths.get("network.har")
        println(harPath.toAbsolutePath())


        val context = browser.newContext(
            Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true) // 3. HTTPS 오류 무시
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        )

        context.routeFromHAR(
            harPath,
            BrowserContext.RouteFromHAROptions().setUpdate(true)
        )

        val page = context.newPage()


        page.onRequest { request ->
            //val url = request.url()
            println(">> Request: ${request.url()} ${request.resourceType()}")

//            if (url.contains("m3u8")) {
//                println("🔗 동영상 URL 감지됨: $url")
//                m3u8FilesList.add(url)
//                val headers = request.headers()
//                val method = request.method()
//                val postData = request.postData()
//                latch.countDown()  // URL을 찾았을 때 래치 해제
//            }
        }
        page.onResponse { response ->
            println("<< Response: ${response.url()} ${response.status()}")
        }
        page.onRequestFailed { request ->
            println("!! Failed: ${request.url()} ${request.failure()}")
        }

        page.navigate(url)

        // 네트워크 활동이 완료될 때까지 대기
        page.waitForLoadState(LoadState.NETWORKIDLE)


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


    fun makeHarFileViaUrl(url: String){

        val playwright = Playwright.create()
        val browser = playwright.chromium().launch(
            BrowserType.LaunchOptions().setHeadless(false) // 브라우저 창 띄우기
        )


        // 브라우저 컨텍스트 생성 (HAR 파일 기록)
        val context = browser.newContext(
            Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setServiceWorkers(ServiceWorkerPolicy.ALLOW)
                //.setRecordHarPath(Paths.get("network_capture.har"))
                //.setRecordHarMode(HarMode.FULL)
        )
        val page = context.newPage()

        page.onRequest { req ->
            println("Main frame: ${req.url()}")
        }

        page.onFrameDetached { req ->
            println("Sub frame: ${req.url()}") // iframe 내부 요청 캡처
        }

        page.onResponse { res ->
            if (res.request().url().contains(".m3u8")) {
                println("HLS detected: ${res.url()}")
            }
        }
        page.navigate(url) // 원하는 웹사이트 접속
        //Thread.sleep(10000) // 모든 네트워크 요청을 캡처할 수 있도록 대기
        page.waitForLoadState(LoadState.NETWORKIDLE)
        page.waitForSelector("video", Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED))
        // 브라우저 종료 및 HAR 저장
        context.close()
        browser.close()
        playwright.close()
    }


}
