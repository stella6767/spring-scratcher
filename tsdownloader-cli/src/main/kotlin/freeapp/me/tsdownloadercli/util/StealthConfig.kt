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


    fun getEnabledScripts(): MutableMap<String, String> {

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


        val scriptsMap = mutableMapOf<String, String>()

        // opts 설정 스크립트 추가
        scriptsMap["opts"] = "const opts = ${gson.toJson(options)};"
        // 조건에 따라 scriptsMap에 추가
        scripts["utils"]?.let { scriptsMap["utils"] = it }
        if (webdriver) scripts["webdriver"]?.let { scriptsMap["webdriver"] = it }
        if (navigatorPlugins) scripts["navigator_plugins"]?.let { scriptsMap["navigator_plugins"] = it }
        if (chromeRuntime) scripts["chrome_runtime"]?.let { scriptsMap["chrome_runtime"] = it }

        scripts["chrome_app"]?.let { if (chromeApp) scriptsMap["chrome_app"] = it }
        scripts["chrome_csi"]?.let { if (chromeCsi) scriptsMap["chrome_csi"] = it }
        scripts["chrome_hairline"]?.let { if (hairline) scriptsMap["chrome_hairline"] = it }
        scripts["chrome_load_times"]?.let { if (chromeLoadTimes) scriptsMap["chrome_load_times"] = it }
        scripts["chrome_runtime"]?.let { if (chromeRuntime) scriptsMap["chrome_runtime"] = it }
        scripts["iframe_content_window"]?.let { if (iframeContentWindow) scriptsMap["iframe_content_window"] = it }
        scripts["media_codecs"]?.let { if (mediaCodecs) scriptsMap["media_codecs"] = it }
        scripts["navigator_languages"]?.let { if (navigatorLanguages) scriptsMap["navigator_languages"] = it }
        scripts["navigator_permissions"]?.let { if (navigatorPermissions) scriptsMap["navigator_permissions"] = it }
        scripts["navigator_platform"]?.let { if (navigatorPlatform) scriptsMap["navigator_platform"] = it }
        scripts["navigator_user_agent"]?.let { if (navigatorUserAgent) scriptsMap["navigator_user_agent"] = it }
        scripts["navigator_vendor"]?.let { if (navigatorVendor) scriptsMap["navigator_vendor"] = it }
        scripts["outerdimensions"]?.let { if (outerDimensions) scriptsMap["outerdimensions"] = it }
        scripts["webgl_vendor"]?.let { if (webglVendor) scriptsMap["webgl_vendor"] = it }


        return scriptsMap
    }



    fun fromFile(name: String): String {

        return ClassPathResource("static/js/$name")
            .inputStream.readBytes().toString(StandardCharsets.UTF_8)
    }

}
