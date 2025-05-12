package freeapp.life.swaggerrestdoc.web.dto

import freeapp.life.swaggerrestdoc.entity.User
import freeapp.life.swaggerrestdoc.util.toStringByFormat
import java.io.File

data class LoginReqDto(
    val email: String,
    val password: String,
)

data class TokenDto (
    val accessToken: String,
)

data class PasswordReqDto(
    val password: String,
)


data class UserResponseDto(
    val id: Long,
    val role: User.Role,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {

        fun fromEntity(user: User): UserResponseDto {

            return UserResponseDto(
                id = user.id,
                role = user.role,
                email = user.email,
                createdAt = user.createdAt.toStringByFormat(),
                updatedAt = user.updatedAt.toStringByFormat()
            )
        }

    }

}
