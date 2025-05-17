package freeapp.life.gitbookdownloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitbookDownloaderApplication

fun main(args: Array<String>) {
    runApplication<GitbookDownloaderApplication>(*args)
}
