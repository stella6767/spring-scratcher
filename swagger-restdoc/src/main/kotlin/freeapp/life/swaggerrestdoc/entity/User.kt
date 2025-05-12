package freeapp.life.swaggerrestdoc.entity

import jakarta.persistence.*


@Entity
@Table(name = "user")
class User(
    username: String,
    email: String,
    password: String,
    role: Role = Role.USER,
) : BaseEntity() {

    @Column(nullable = false)
    val username = username

    @Column(nullable = false, length = 100)
    val email = email

    @Column(nullable = false, length = 100)
    val password = password

    @Enumerated(EnumType.STRING)
    val role = role


    enum class Role(
        val value:String
    ) {

        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN"),
    }

}
