package freeapp.me.tsdownloadercli.service

import java.nio.charset.StandardCharsets

class PlaywrightStealth(

) {

    fun fromFile(name: String): String {
        val stream = object {}.javaClass.getResourceAsStream("/js/$name")
            ?: throw IllegalArgumentException("Resource /js/$name not found")
        return stream.readBytes().toString(StandardCharsets.UTF_8)
    }




}
