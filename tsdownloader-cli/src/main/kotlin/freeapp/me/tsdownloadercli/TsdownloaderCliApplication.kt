package freeapp.me.tsdownloadercli

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TsdownloaderCliApplication

fun main(args: Array<String>) {
    runApplication<TsdownloaderCliApplication>(*args)
}
