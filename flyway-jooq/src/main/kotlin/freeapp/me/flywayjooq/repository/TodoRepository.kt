package freeapp.me.flywayjooq.repository

import freeapp.me.flywayjooq.dto.PageImpl
import freeapp.me.flywayjooq.dto.TodoDto
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.generated.tables.Todo.Companion.TODO_
import org.jooq.generated.tables.records.TodoRecord
import org.springframework.stereotype.Repository


@Repository
class TodoRepository(
    private val dslContext: DSLContext,
) {

    fun test() {
        val id =
            dslContext.select(TODO_.ID)
                .from(TODO_)
                .fetchInto(Long::class.java)

        println(id)
    }


    fun findById(id: Int): TodoRecord? {
        return dslContext.select(*TODO_.fields())
            .from(TODO_)
            .where(TODO_.ID.eq(id))
            .fetchOneInto(TodoRecord::class.java)
    }

    fun findByIdAsToDto(id: Int): TodoDto? {
        return dslContext.select(*TODO_.fields())
            .from(TODO_)
            .where(TODO_.ID.eq(id))
            .fetchOneInto(TodoDto::class.java)
    }

    fun findTodosByPage(page:Long, pageSize:Long): PageImpl<MutableList<TodoDto>> {

        val list = dslContext.select(*TODO_.fields())
            .from(TODO_)
            .orderBy(TODO_.ID)
            .limit(pageSize)
            .offset(page * pageSize)
            .fetchInto(TodoDto::class.java)

        val totalCount = dslContext.selectCount()
            .from(TODO_)
            .fetchOne(0, Long::class.java) ?: 0

        return PageImpl(page, pageSize, totalCount, list)
    }


    fun findByContentAsToDtos(content: String?): MutableList<TodoDto> {

        return dslContext.select(*TODO_.fields())
            .from(TODO_)
            .where(TODO_.CONTENT.eqifNotNull(content))
            .fetchInto(TodoDto::class.java)
    }

}



