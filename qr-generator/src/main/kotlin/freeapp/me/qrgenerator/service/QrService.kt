package freeapp.me.qrgenerator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import freeapp.me.qrgenerator.config.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.HashMap


@Service
class QrService(
    private val mapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(QrService::class.java)


    fun generateStaticQRCodeByType(
        type: QrGeneratorType,
        qrReqDto: HashMap<String, Any>
    ): String {
        return when (type) {
            QrGeneratorType.LINK, QrGeneratorType.TEXT -> {
                val textReqDto =
                    mapper.convertValue(qrReqDto, TextReqDto::class.java)
                generateStaticQRCode(textReqDto.text)
            }
            QrGeneratorType.SMS -> {

                TODO()
            }
            QrGeneratorType.WIFI -> {
                val vCardDto =
                    mapper.convertValue(qrReqDto, WifiReqDto::class.java)

                TODO()
            }
            QrGeneratorType.VCARD -> {
                val vCardDto =
                    mapper.convertValue(qrReqDto, VCardReqDto::class.java)
                val vCard = """
                    BEGIN:VCARD
                    VERSION:3.0
                    FN:${vCardDto.firstName}  ${vCardDto.lastName}
                    TEL;TYPE=CELL:${vCardDto.phoneNumber}                   
                    END:VCARD
                """.trimIndent()
                println(vCard)
                generateStaticQRCode(vCard)
            }
            QrGeneratorType.TEL -> {
                val callDto =
                    mapper.convertValue(qrReqDto, CallReqDto::class.java)
                TODO()
            }
        }

    }


    fun generateStaticQRCode(
        qrValue: String,
        width: Int = 300,
        height: Int = 300
    ): String {

        val qrCodeWriter = QRCodeWriter()
        val bitMatrix =
            qrCodeWriter.encode(qrValue, BarcodeFormat.QR_CODE, width, height)

        val outputStream = ByteArrayOutputStream()

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)

        val base64Image =
            Base64.getEncoder().encodeToString(outputStream.toByteArray())

        //MatrixToImageWriter.writeToPath(bitMatrix, "PNG", Paths.get("./test.png"))
        return base64Image
    }


}
