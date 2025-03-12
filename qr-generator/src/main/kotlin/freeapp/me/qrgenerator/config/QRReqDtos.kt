package freeapp.me.qrgenerator.config


data class LinkReqDto(
    val url:String,
)

data class VCardReqDto(
    val firstName: String,
    val lastName: String,
    val phoneNumber:String,
)
