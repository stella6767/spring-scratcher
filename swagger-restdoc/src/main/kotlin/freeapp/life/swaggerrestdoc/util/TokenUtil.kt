package freeapp.life.swaggerrestdoc.util


import jakarta.servlet.http.HttpServletRequest
import org.springframework.util.StringUtils
import java.security.SecureRandom
import java.util.*

object TokenUtil {

    /**
     * jwt 아닌 기타 유틸리티 token function 집합
     */

    private const val VERIFY_CODE_INT_LENGTH = 6
    private const val PASSWORD_INT_LENGTH = 6
    private const val AUTHORIZATION_HEADER = "Authorization"
    const val BEARER_PREFIX = "Bearer "

    fun createToken(bytesLength: Int): String {
        val random = SecureRandom()
        val bytes = ByteArray(bytesLength)
        random.nextBytes(bytes)
        val encoder = Base64.getUrlEncoder().withoutPadding()
        return encoder.encodeToString(bytes)
    }


    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken =
            request.getHeader(AUTHORIZATION_HEADER)

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(7)
        } else null
    }


    fun generateRandomPassword(): String {
        return getIntRandomString(PASSWORD_INT_LENGTH)
    }

    fun generateVerifyCode(): String {
        // 6자리 랜덤한 숫자문자열 생성
        return getIntRandomString(VERIFY_CODE_INT_LENGTH)
    }

    private fun getIntRandomString(targetStringLength: Int): String {
        val leftLimit = 48 // numeral '0'
        val rightLimit = 57 // letter '9'

        val random = Random()
        return random.ints(leftLimit, rightLimit + 1)
            .filter { i: Int -> i <= 57 || i >= 65 }
            .limit(targetStringLength.toLong())
            .collect(
                { StringBuilder() },
                { obj: StringBuilder, codePoint: Int -> obj.appendCodePoint(codePoint) }
            ) { obj: StringBuilder, s: StringBuilder ->
                obj.append(
                    s
                )
            }
            .toString()
    }

}
