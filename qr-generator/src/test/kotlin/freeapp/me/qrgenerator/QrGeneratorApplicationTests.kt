package freeapp.me.qrgenerator

import freeapp.me.qrgenerator.service.QrService
import org.junit.jupiter.api.Test

//@SpringBootTest
class QrGeneratorApplicationTests {

    private val qrService = QrService()

    @Test
    fun contextLoads() {

        val generateQRCode =
            qrService.generateStaticQRCode(text = "https://www.freeapp.me/")

        println(generateQRCode)

    }

}
