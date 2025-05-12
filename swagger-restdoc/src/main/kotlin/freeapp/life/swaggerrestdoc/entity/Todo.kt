package freeapp.life.swaggerrestdoc.entity

import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.*

@Entity
@Table(name = "todo")
class Todo(
    id: Long = 0,
    content: String,
    isFinish: Boolean = false,
    status: Status
) : BaseEntity(id = id) {


    @Column(name = "content", length = 500, nullable = false)
    var content = content

    @Column(name = "isFinish")
    var isFinish = isFinish

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status = status

    fun update(
        status: Status,
        content: String,
        finish: Boolean
    ) {

        this.status = status
        this.content = content
        this.isFinish = finish
    }


    enum class Status(
        @JsonValue
        val value: String
    ) {
        URGENT("긴급"),
        NORMAL("일반"),
        MINOR("경미")
    }

}
