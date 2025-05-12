package freeapp.life.swaggerrestdoc.web.dto

import freeapp.life.swaggerrestdoc.entity.Todo
import freeapp.life.swaggerrestdoc.entity.User
import freeapp.life.swaggerrestdoc.util.toStringByFormat

data class TodoSaveDto(
    val content: String,
    val status: Todo.Status,
){

    fun toEntity(): Todo {
        return Todo(
            content = content,
            status = status,
        )
    }

}

data class TodoUpdateDto(
    val id: Long,
    val content: String,
    val status: Todo.Status,
    val isFinish: Boolean,
)


data class TodoResponseDto(
    val id: Long,
    val content: String,
    val status: Todo.Status,
    val isFinish: Boolean,
    val createdAt: String,
    val updatedAt: String,
) {
    companion object {

        fun fromEntity(todo: Todo): TodoResponseDto {

            return TodoResponseDto(
                id = todo.id,
                content = todo.content,
                status = todo.status,
                isFinish = todo.isFinish,
                createdAt = todo.createdAt.toStringByFormat(),
                updatedAt = todo.updatedAt.toStringByFormat()
            )
        }
    }
}
