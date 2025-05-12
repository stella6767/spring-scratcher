package freeapp.life.swaggerrestdoc.repo

import freeapp.life.swaggerrestdoc.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}
