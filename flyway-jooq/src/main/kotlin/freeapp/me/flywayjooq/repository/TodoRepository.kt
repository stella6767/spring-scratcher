package freeapp.me.flywayjooq.repository

import org.jooq.DSLContext
import org.jooq.generated.tables.Todo
import org.jooq.generated.tables.Todo.Companion.TODO_

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






}
