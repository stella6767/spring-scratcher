package freeapp.life.swaggerrestdoc.web.dto

import freeapp.life.swaggerrestdoc.entity.User
import freeapp.life.swaggerrestdoc.util.toStringByFormat
import jakarta.validation.constraints.NotBlank
import java.io.File

data class LoginReqDto(
    @field:NotBlank(message = "email is required")
    val email: String,
    @field:NotBlank(message = "password is required")
    val password: String,
)

data class TokenDto (
    val accessToken: String,
)

data class PasswordReqDto(
    @field:NotBlank(message = "email is required")
    val password: String,
)


data class UserResponseDto(
    val id: Long,
    val role: User.Role,
    @field:NotBlank(message = "email is required")
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
