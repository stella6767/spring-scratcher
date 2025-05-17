package freeapp.life.tawilwindjtetemplate.web

import ch.qos.logback.core.model.Model
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController(

) {

    @GetMapping("/")
    fun index(model: Model): String {

        return "index"
    }

}
