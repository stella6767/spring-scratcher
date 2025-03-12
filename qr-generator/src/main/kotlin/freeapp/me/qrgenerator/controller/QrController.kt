package freeapp.me.qrgenerator.controller

import freeapp.me.qrgenerator.config.QRReqDto
import freeapp.me.qrgenerator.config.QrGeneratorType
import freeapp.me.qrgenerator.service.QrService
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
class QrController(
    private val qrService: QrService,
) {


    @GetMapping("/")
    fun index(
        model: Model
    ): String {

        val toList = QrGeneratorType.entries.toList()

        //model.addAttribute("types", QrGeneratorType.values().toList())

        return "page/index"
    }


    @HxRequest
    @PostMapping("/qrcode")
    @ResponseBody
    fun qrcode(
        qrReqDto: QRReqDto
    ) {



    }


}
