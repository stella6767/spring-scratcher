package freeapp.life.swaggerrestdoc.web.dto

data class SuccessResponse<T>(
    var resultMsg: String,
    var data: T
)
