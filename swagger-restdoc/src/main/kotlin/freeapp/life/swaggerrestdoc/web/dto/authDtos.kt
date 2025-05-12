package freeapp.life.swaggerrestdoc.web.dto

import freeapp.life.swaggerrestdoc.entity.User
import freeapp.life.swaggerrestdoc.util.EMAIL_PATTERN
import freeapp.life.swaggerrestdoc.util.toStringByFormat
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class RegisterDto(
    @field:NotBlank(message = "username is required")
    val username: String,
    @field:NotBlank(message = "email is required")
    @field:Email(regexp = EMAIL_PATTERN, message = "not valid email")
    val email: String,
    @field:NotBlank(message = "password is required")
    val password: String,
) {
    fun toEntity(encodedPassword: String): User {

        return User(
            username = this.username,
            email = this.email,
            password = encodedPassword
        )
    }
}


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
    val username: String,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {

        fun fromEntity(user: User): UserResponseDto {

            return UserResponseDto(
                id = user.id,
                role = user.role,
                email = user.email,
                username = user.username,
                createdAt = user.createdAt.toStringByFormat(),
                updatedAt = user.updatedAt.toStringByFormat()
            )
        }

    }

}
