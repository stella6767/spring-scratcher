package freeapp.me.qrgenerator.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO


@Service
class QrService(

) {

    private val log = LoggerFactory.getLogger(QrService::class.java)

    fun generateQRCode(text: String, width: Int = 300, height: Int = 300) {

        val bitMatrix =
            MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)

        // BufferedImage로 변환
        val bufferedImage =
            MatrixToImageWriter.toBufferedImage(bitMatrix)

        val file = File("./test.png")
        ImageIO.write(bufferedImage, "PNG", file)

    }

}
