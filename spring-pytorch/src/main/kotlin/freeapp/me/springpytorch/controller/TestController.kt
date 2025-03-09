package freeapp.me.springpytorch.controller

import ai.djl.modality.cv.ImageFactory
import freeapp.me.springpytorch.service.PyTorchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.awt.Image


@RestController
class TestController(
    private val pyTorchService: PyTorchService
) {

    @GetMapping("/test")
    fun test(): String {
        var img =
            ImageFactory.getInstance().fromUrl("https://raw.githubusercontent.com/pytorch/hub/master/images/dog.jpg")
        img.wrappedImage
        return pyTorchService.classify(img).toJson()
    }


}
