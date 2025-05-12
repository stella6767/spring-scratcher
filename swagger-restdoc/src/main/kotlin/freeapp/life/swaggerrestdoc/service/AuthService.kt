package freeapp.life.swaggerrestdoc.service

import freeapp.life.swaggerrestdoc.config.security.JwtTokenProvider
import freeapp.life.swaggerrestdoc.config.security.UserPrincipal
import freeapp.life.swaggerrestdoc.repo.UserRepository
import freeapp.life.swaggerrestdoc.web.dto.*
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory

import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val tokenProvider: JwtTokenProvider,
) {

    private val log = LoggerFactory.getLogger(this::class.java)


    @Transactional
    fun register(
        dto: RegisterDto,
    ): UserResponseDto {

        val encodedPassword =
            encoder.encode(dto.password)

        return UserResponseDto.fromEntity(userRepository.save(dto.toEntity(encodedPassword)))
    }

    @Transactional
    fun login(loginDto: LoginReqDto): TokenDto {

        log.info("login!!!! $loginDto")

        val user =
            userRepository.findByEmail(loginDto.email)
                ?: throw BadCredentialsException("자격 증명에 실패하였습니다.")

        if (!encoder.matches(loginDto.password, user.password)) {
            throw BadCredentialsException("자격 증명에 실패하였습니다.")
        }

        val payload =
            mutableMapOf<String, String>()

        payload["userId"] = user.id.toString()
        payload["username"] = user.username
        payload["role"] = user.role.name
        payload["email"] = user.email
        payload["authorities"] = user.role.value

        val jwt = createJWT(payload, user.email)

        return TokenDto(jwt)
    }

    private fun createJWT(
        payload: MutableMap<String, String>,
        email: String
    ): String {

        val accessToken =
            tokenProvider.createToken(payload, email)

        return accessToken
    }


    @Transactional(readOnly = true)
    fun loadUser(principal: UserPrincipal): UserResponseDto {

        val userId = principal.getUserId()

        val user =
            userRepository.findByIdOrNull(userId) ?: throw EntityNotFoundException("cant find user by id")

        return UserResponseDto.fromEntity(user)
    }


    @Transactional(readOnly = true)
    fun validatePassword(
        principal: UserPrincipal,
        password: PasswordReqDto
    ): UserResponseDto {

        val user =
            userRepository.findByIdOrNull(principal.getUserId())
                ?: throw EntityNotFoundException("cant find user by id")

        if (encoder.matches(password.password, user.password)) {
            log.info("일치합니다.")
            return UserResponseDto.fromEntity(user)
        } else {
            log.info("불일치합니다.")
            throw IllegalArgumentException("user password encoder does not match")
        }
    }


}
