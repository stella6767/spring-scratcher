package freeapp.me.qrgenerator.config

//https://emojipedia.org/

enum class QrGeneratorType(
    val icon: String,
    val fieldName: String,
) {

    LINK("\uD83D\uDD17", "Link"),
    TEXT("\uD83D\uDCC4 ", "Text"),
    WIFI("\uD83D\uDCDE", "Wifi"),
    VCARD("\uD83D\uDCE9", "V-Card"),
    TEL("\uD83D\uDEDC", "Tel"),
}


