package freeapp.me.qrgenerator.config

//https://emojipedia.org/

enum class QrGeneratorType(
    val icon: String,
    val fieldName: String,
) {

    LINK("\uD83D\uDD17", "Link"),
    TEXT("\uD83D\uDCC4 ", "Text"),
    WIFI( "\uD83D\uDEDC", "Wifi"),
    VCARD("🪪", "V-Card"),
    TEL("\uD83D\uDCDE", "Tel"),
    PDF("\uD83D\uDCDC", "PDF"),

//    todo 귀찮으니까 나중에
//    IMAGES("\uD83D\uDCDE", "Images"),
//    PROFILE_CARD("\uD83D\uDCDE", "Profile Card"),

}


