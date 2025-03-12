package freeapp.me.qrgenerator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import freeapp.me.qrgenerator.config.LinkReqDto
import freeapp.me.qrgenerator.config.QrGeneratorType
import freeapp.me.qrgenerator.config.VCardReqDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam
import java.io.File
import java.nio.file.Paths
import javax.imageio.ImageIO


@Service
class QrService(
    private val mapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(QrService::class.java)


    fun generateStaticQRCodeByType(
        type: QrGeneratorType,
        qrReqDto: HashMap<String, Any>
    ) {
        when (type) {
            QrGeneratorType.LINK -> {
                val linkReqDto =
                    mapper.convertValue(qrReqDto, LinkReqDto::class.java)
                generateStaticQRCode(linkReqDto.url)
            }

            QrGeneratorType.TEXT -> TODO()
            QrGeneratorType.SMS -> TODO()
            QrGeneratorType.WIFI -> TODO()
            QrGeneratorType.VCARD -> {
                val vCardDto =
                    mapper.convertValue(qrReqDto, VCardReqDto::class.java)
                val vCard = """
                    BEGIN:VCARD
                    VERSION:3.0
                    FN:${vCardDto.firstName}  ${vCardDto.lastName}
                    TEL:${vCardDto.phoneNumber}                   
                    END:VCARD
                """.trimIndent()
                println(vCard)
                generateStaticQRCode(vCard)
            }

            QrGeneratorType.TEL -> TODO()
            QrGeneratorType.TELEGRAM -> TODO()
        }

    }


    fun generateStaticQRCode(
        text: String,
        width: Int = 300,
        height: Int = 300
    ) {

        val qrCodeWriter = QRCodeWriter()

        val bitMatrix =
            qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)

        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get("./test.png"))
    }

}
