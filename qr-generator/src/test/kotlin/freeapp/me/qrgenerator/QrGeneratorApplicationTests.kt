package freeapp.me.qrgenerator

import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.me.qrgenerator.service.QrService
import freeapp.me.qrgenerator.service.S3Service
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestConstructor
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths


@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class QrGeneratorApplicationTests(
    private val s3Service: S3Service
) {

    private val qrService = QrService(ObjectMapper())

    @Test
    fun putObjectTest(){

        val path= Paths.get("src/main/resources/static/favicon.jpeg")

        val name = "favicon.jpg"
        val originalFileName = "favicon.jpg"
        val contentType = MediaType.IMAGE_JPEG_VALUE
        val content = Files.readAllBytes(path)

        val mockFile: MultipartFile = MockMultipartFile(
            name,
            originalFileName, contentType, content
        )

        val putObject = s3Service.putObject(mockFile)

        println("!!!")
        println(putObject)
    }

    @Test
    fun contextLoads() {

        val generateQRCode =
            qrService.generateStaticQRCode(qrValue = "https://www.freeapp.me/")

        println(generateQRCode)

    }

}
