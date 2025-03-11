package freeapp.me.tsdownloadercli.util

data class HttpReqHeaderDto(
    val name: String,
    val value: String
) {

}


data class M3u8RequestDto(
    val reqHeaders: List<HttpReqHeaderDto>,
    var m3u8file: String
) {

    //var highestBitrateUrl = ""
}
