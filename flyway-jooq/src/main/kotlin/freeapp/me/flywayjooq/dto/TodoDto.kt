package freeapp.me.flywayjooq.dto

//https://cheese10yun.github.io/kotlin-pattern/
data class TodoDto(
    val id:Long = 0,
    val content:String,
    val status:Boolean = false,
)  {

}
