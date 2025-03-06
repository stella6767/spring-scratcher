package freeapp.me.tsdownloadercli.cli

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption


@ShellComponent
class DownloaderCli {


    @ShellMethod(key = ["hello"], value = "welcome")
    fun test(
        @ShellOption("-n", defaultValue = "world") name:String
    ){
        println( "hello $name")
    }



}
