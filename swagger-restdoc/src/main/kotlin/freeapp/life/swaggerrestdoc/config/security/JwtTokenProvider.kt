package freeapp.life.swaggerrestdoc.config.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.WeakKeyException
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.TimeUnit


@Component
class JwtTokenProvider(

) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${jwt.secretKey}")
    private lateinit var jwtKey: String

    @Value("\${jwt.expireMinute}")
    private lateinit var expireMinute: String

    private val secretKey by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtKey))
    }

    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (ex: Exception) {
            when (ex) {
                is WeakKeyException -> {
                    log.warn(ex.localizedMessage)
                    return true
                }

                is SecurityException -> log.warn("잘못된 JWT 서명입니다.")
                is MalformedJwtException -> log.warn("잘못된 JWT 서명입니다.")
                is ExpiredJwtException -> log.warn("만료된 JWT 서명입니다.")
                is UnsupportedJwtException -> log.warn("지원되지 않는 JWT 토큰입니다.")
                is IllegalArgumentException -> log.warn("JWT 토큰이 잘못되었습니다.")
                else -> log.error(ex.localizedMessage)
            }
        }
        return false
    }


    fun getAuthentication(token: String): Authentication {

        val claims: Claims =
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload

        //권한 정보 가져오기
        val authorities: MutableCollection<SimpleGrantedAuthority> =
            claims["authorities"].toString().split(",")
                .map { role: String -> SimpleGrantedAuthority(role) }.toMutableList()

        val payload: Map<String, String> = claims.entries.associate { (key, value) ->
            key to value.toString()
        }

        val principal = UserPrincipal(payload)
        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }


    fun getUserId(token: String): Long {
        val jws = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
        val bodyClaims: Claims = jws.payload
        return bodyClaims.subject.toString().toLong()
    }


    fun createToken(claims: Map<String, Any?>, subject: String): String {

        val currentTime = System.currentTimeMillis()
        val expireTime =
            TimeUnit.MINUTES.toMillis(expireMinute.toLong())

        log.info("create jwt")

        return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .claims(claims)
            .notBefore(Date(currentTime))
            .subject(subject)
            .issuedAt(Date(currentTime))
            .expiration(Date(currentTime + expireTime))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }


}
