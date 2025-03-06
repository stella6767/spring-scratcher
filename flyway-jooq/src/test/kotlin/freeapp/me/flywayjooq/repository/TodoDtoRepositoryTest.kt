package freeapp.me.flywayjooq.repository

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import kotlin.test.Test


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TodoDtoRepositoryTest(
    private val todoRepository: TodoRepository,
) {

    @Test
    fun test() {
        todoRepository.test()
    }

    @Test
    fun findByIdTest() {

        val todoRecord =
            todoRepository.findById(1)

        val todo =
            todoRepository.findByContentAsToDtos(null)

        println(todoRecord)
        println(todo)
    }


    @Test
    fun findTodosByPageTest(){

        val todosByPage =
            todoRepository.findTodosByPage(1, 10)

        println(todosByPage)

    }


}
