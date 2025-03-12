package freeapp.me.qrgenerator

import freeapp.me.qrgenerator.service.QrService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

//@SpringBootTest
class QrGeneratorApplicationTests {

    private val qrService = QrService()

    @Test
    fun contextLoads() {

        val generateQRCode =
            qrService.generateQRCode(text = "test")

        println(generateQRCode)

    }

}
