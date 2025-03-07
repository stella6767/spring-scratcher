package freeapp.me.tsdownloadercli.util
import com.microsoft.playwright.Page

object PlaywrightStealth {
    fun applyStealth(page: Page, config: StealthConfig = StealthConfig()) {
        config.getEnabledScripts().forEach { script ->
            page.addInitScript(script)
        }
    }

}
