package freeapp.me.tsdownloadercli.util
import com.microsoft.playwright.Page

object PlaywrightStealth {
    fun applyStealth(page: Page, config: StealthConfig = StealthConfig()) {
        var totalScript = ""
        config.getEnabledScripts().forEach { script ->
            println(script.key)
            totalScript += script.value
        }
        page.addInitScript(totalScript)
        //page.context().newCDPSession(page).send(totalScript)
        //page.addInitScript(config.fromFile("test.js"))
    }

}
