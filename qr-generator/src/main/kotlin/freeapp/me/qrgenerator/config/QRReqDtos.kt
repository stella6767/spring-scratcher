package freeapp.me.qrgenerator.config


data class TextReqDto(
    val text:String,
)

data class VCardReqDto(
    val firstName: String,
    val lastName: String,
    val phoneNumber:String,
)


data class WifiReqDto(
    val ssid: String,
    val encryption: String,
    val password:String,
)


data class CallReqDto(
    val countryCode: String,
    val phoneNumber:String,
)
