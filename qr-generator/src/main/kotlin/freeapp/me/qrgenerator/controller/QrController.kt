package freeapp.me.qrgenerator.controller

import freeapp.me.qrgenerator.config.QrGeneratorType
import freeapp.me.qrgenerator.service.QrService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.FragmentsRendering


@Controller
class QrController(
    private val qrService: QrService,
) {


    @GetMapping("/")
    fun index(
        model: Model
    ): String {

        return "page/index"
    }

    //todo converter && exception handling & dynamic with s3
    //

    @HxRequest
    @GetMapping("/qr/{type}")
    fun qrType(
        @PathVariable type: QrGeneratorType,
        model: Model,
    ): FragmentsRendering {

        model.addAttribute("type", type)

        return FragmentsRendering
            .with("component/qrMainInput")
            .fragment("component/qrCodeBtn")
            .build()
    }


    @HxRequest
    @PostMapping("/qrcode")
    fun qrcode(
        model: Model,
        @RequestParam type: QrGeneratorType,
        @RequestParam qrReqDto: HashMap<String, Any>
    ): FragmentsRendering {

        val qrCode =
            qrService.generateStaticQRCodeByType(type, qrReqDto)

        model.addAttribute("imageData", qrCode)
        model.addAttribute("isGenerated", true)

        return FragmentsRendering
            .with("component/qrImg")
            .fragment("component/qrCodeBtn")
            .build()
    }


}
