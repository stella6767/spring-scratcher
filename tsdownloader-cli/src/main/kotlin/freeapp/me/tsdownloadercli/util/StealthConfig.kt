package freeapp.me.tsdownloadercli.util

import com.google.gson.Gson
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

    private val scripts: Map<String, String> = mapOf(
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


    fun getEnabledScripts(): List<String> {

        val options = mapOf(
            "webgl_vendor" to vendor,
            "webgl_renderer" to renderer,
            "navigator_vendor" to navVendor,
            "navigator_platform" to navPlatform,
            "navigator_user_agent" to navUserAgent,
            "languages" to languages,
            "runOnInsecureOrigins" to runOnInsecureOrigins
        )

        val gson = Gson()
        val jsScripts = mutableListOf<String>()

        jsScripts.add("const opts = ${gson.toJson(options)};")
        scripts["utils"]?.let { jsScripts.add(it) }
        if (webdriver) scripts["webdriver"]?.let { jsScripts.add(it) }
        if (navigatorPlugins) scripts["navigator_plugins"]?.let { jsScripts.add(it) }
        if (chromeRuntime) scripts["chrome_runtime"]?.let { jsScripts.add(it) }

        scripts["chrome_app"]?.let { if (chromeApp) jsScripts.add(it) }
        scripts["chrome_csi"]?.let { if (chromeCsi) jsScripts.add(it) }
        scripts["chrome_hairline"]?.let { if (hairline) jsScripts.add(it) }
        scripts["chrome_load_times"]?.let { if (chromeLoadTimes) jsScripts.add(it) }
        scripts["chrome_runtime"]?.let { if (chromeRuntime) jsScripts.add(it) }
        scripts["iframe_content_window"]?.let { if (iframeContentWindow) jsScripts.add(it) }
        scripts["media_codecs"]?.let { if (mediaCodecs) jsScripts.add(it) }
        scripts["navigator_languages"]?.let { if (navigatorLanguages) jsScripts.add(it) }
        scripts["navigator_permissions"]?.let { if (navigatorPermissions) jsScripts.add(it) }
        scripts["navigator_platform"]?.let { if (navigatorPlatform) jsScripts.add(it) }
        scripts["navigator_plugins"]?.let { if (navigatorPlugins) jsScripts.add(it) }
        scripts["navigator_user_agent"]?.let { if (navigatorUserAgent) jsScripts.add(it) }
        scripts["navigator_vendor"]?.let { if (navigatorVendor) jsScripts.add(it) }
        scripts["webdriver"]?.let { if (webdriver) jsScripts.add(it) }
        scripts["outerdimensions"]?.let { if (outerDimensions) jsScripts.add(it) }
        scripts["webgl_vendor"]?.let { if (webglVendor) jsScripts.add(it) }

        return jsScripts
    }



    fun fromFile(name: String): String {

        return ClassPathResource("static/js/$name")
            .inputStream.readBytes().toString(StandardCharsets.UTF_8)
    }

}
