package freeapp.me.qrgenerator.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import freeapp.me.qrgenerator.config.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.*


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
            QrGeneratorType.WIFI -> {
                val wifiDto =
                    mapper.convertValue(qrReqDto, WifiReqDto::class.java)
                val qrValue = "WIFI:T:${wifiDto.encryption};S:${wifiDto.ssid};P:${wifiDto.password};;"
                println(qrValue)
                generateStaticQRCode(qrValue)
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
                val qrValue = "tel:${callDto.countryCode}${callDto.phoneNumber}"
                println(qrValue)
                generateStaticQRCode(qrValue)
            }
        }

    }


    fun generateStaticQRCode(
        qrValue: String,
        width: Int = 300,
        height: Int = 300
    ): String {

        val qrCodeWriter = QRCodeWriter()

        val hintMap: MutableMap<EncodeHintType, Any> = HashMap()
        hintMap[EncodeHintType.MARGIN] = 0
        hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"

        val bitMatrix =
            qrCodeWriter.encode(qrValue, BarcodeFormat.QR_CODE, width, height, hintMap)

        val outputStream = ByteArrayOutputStream()

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream)

        val base64Image =
            Base64.getEncoder().encodeToString(outputStream.toByteArray())

        return base64Image
    }


}
