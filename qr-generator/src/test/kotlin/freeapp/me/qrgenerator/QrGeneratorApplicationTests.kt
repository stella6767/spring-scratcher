package freeapp.me.qrgenerator

import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.me.qrgenerator.service.QrService
import org.junit.jupiter.api.Test

//@SpringBootTest
class QrGeneratorApplicationTests {

    private val qrService = QrService(ObjectMapper())

    @Test
    fun contextLoads() {

        val generateQRCode =
            qrService.generateStaticQRCode(text = "https://www.freeapp.me/")

        println(generateQRCode)

    }

}
