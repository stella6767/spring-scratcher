package freeapp.life.swaggerrestdoc.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class UserPrincipal(
    val claims: Map<String, String>,
) : UserDetails {

    private val authorities: MutableCollection<out GrantedAuthority> =
        Collections.singletonList(SimpleGrantedAuthority(claims["authorities"]))

    fun getUserId(): Long {
        return claims["userId"]?.toLong() ?: 0
    }

    fun getRole(): String {
        return claims["role"] ?: ""
    }

    fun getEmail(): String {
        return claims["email"] ?: ""
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getUsername(): String {
        return claims["username"] ?: ""
    }

    override fun getPassword(): String {
        return ""
    }


    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun toString(): String {
        return "UserPrincipal(claims=$claims, authorities=$authorities)"
    }


}
