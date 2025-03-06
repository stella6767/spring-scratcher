package freeapp.me.flywayjooq.repository

import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor
import kotlin.test.Test


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class TodoRepositoryTest(
    private val todoRepository: TodoRepository,
) {

    @Test
    fun test() {
        todoRepository.test()

    }


}
