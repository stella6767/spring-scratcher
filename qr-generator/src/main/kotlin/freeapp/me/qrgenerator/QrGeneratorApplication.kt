package freeapp.me.qrgenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QrGeneratorApplication

fun main(args: Array<String>) {
    runApplication<QrGeneratorApplication>(*args)
}
