package freeapp.me.qrgenerator.controller

import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.me.qrgenerator.config.LinkReqDto

import freeapp.me.qrgenerator.config.QrGeneratorType
import freeapp.me.qrgenerator.service.QrService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
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

    //todo converter && exception handling

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
    @ResponseBody
    fun qrcode(
        @RequestParam type: QrGeneratorType,
        @RequestParam qrReqDto: HashMap<String, Any>
    ) {

        println(qrReqDto)

        qrService.generateStaticQRCodeByType(type, qrReqDto)
    }


}
