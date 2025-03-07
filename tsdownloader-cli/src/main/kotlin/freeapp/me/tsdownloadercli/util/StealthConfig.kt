package freeapp.me.tsdownloadercli.util

import getText
import org.springframework.core.io.ClassPathResource
import java.nio.charset.StandardCharsets

class StealthConfig(
    val webdriver: Boolean = true,
    val webglVendor: Boolean = true,
    val chromeApp: Boolean = true,
    val chromeCsi: Boolean = true,
    val chromeLoadTimes: Boolean = true,
    val chromeRuntime: Boolean = true,
    val iframeContentWindow: Boolean = true,
    val mediaCodecs: Boolean = true,
    val navigatorHardwareConcurrency: Int = 4,
    val navigatorLanguages: Boolean = true,
    val navigatorPermissions: Boolean = true,
    val navigatorPlatform: Boolean = true,
    val navigatorPlugins: Boolean = true,
    val navigatorUserAgent: Boolean = true,
    val navigatorVendor: Boolean = true,
    val outerDimensions: Boolean = true,
    val hairline: Boolean = true,
    val vendor: String = "Intel Inc.",
    val renderer: String = "Intel Iris OpenGL Engine",
    val navVendor: String = "Google Inc.",
    val navUserAgent: String? = null,
    val navPlatform: String? = null,
    val languages: List<String> = listOf("en-US", "en"),
    val runOnInsecureOrigins: Boolean? = null
) {

    private val SCRIPTS: Map<String, String> = mapOf(
        "chrome_csi" to fromFile("chrome.csi.js"),
        "chrome_app" to fromFile("chrome.app.js"),
        "chrome_runtime" to fromFile("chrome.runtime.js"),
        "chrome_load_times" to fromFile("chrome.load.times.js"),
        "chrome_hairline" to fromFile("chrome.hairline.js"),
        "generate_magic_arrays" to fromFile("generate.magic.arrays.js"),
        "iframe_content_window" to fromFile("iframe.contentWindow.js"),
        "media_codecs" to fromFile("media.codecs.js"),
        "navigator_vendor" to fromFile("navigator.vendor.js"),
        "navigator_plugins" to fromFile("navigator.plugins.js"),
        "navigator_permissions" to fromFile("navigator.permissions.js"),
        "navigator_languages" to fromFile("navigator.languages.js"),
        "navigator_platform" to fromFile("navigator.platform.js"),
        "navigator_user_agent" to fromFile("navigator.userAgent.js"),
        "navigator_hardware_concurrency" to fromFile("navigator.hardwareConcurrency.js"),
        "outerdimensions" to fromFile("window.outerdimensions.js"),
        "utils" to fromFile("utils.js"),
        "webdriver" to fromFile("navigator.webdriver.js"),
        "webgl_vendor" to fromFile("webgl.vendor.js")
    )



//    fun getEnabledScripts(): List<String> {
//        val options = mapOf(
//            "webgl_vendor" to vendor,
//            "webgl_renderer" to renderer,
//            "navigator_vendor" to navVendor,
//            "navigator_platform" to navPlatform,
//            "navigator_user_agent" to navUserAgent,
//            "languages" to languages,
//            "runOnInsecureOrigins" to runOnInsecureOrigins
//        )
//
//        val jsScripts = mutableListOf<String>()
//        jsScripts.add("const opts = ${Json.encodeToString(options)};")
//        jsScripts.add(scripts["utils"]!!)
//
//        if (webdriver) jsScripts.add(scripts["webdriver"]!!)
//        if (navigatorPlugins) jsScripts.add(scripts["navigator_plugins"]!!)
//        if (chromeRuntime) jsScripts.add(scripts["chrome_runtime"]!!)
//
//        return jsScripts
//    }


    fun fromFile(name: String): String {

        return ClassPathResource("static/js/$name").inputStream.readBytes().toString(StandardCharsets.UTF_8)
    }

}
