package freeapp.life.swaggerrestdoc.service

import freeapp.life.swaggerrestdoc.entity.Todo
import freeapp.life.swaggerrestdoc.repo.TodoRepository
import freeapp.life.swaggerrestdoc.web.dto.TodoResponseDto
import freeapp.life.swaggerrestdoc.web.dto.TodoSaveDto
import freeapp.life.swaggerrestdoc.web.dto.TodoUpdateDto
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TodoService(
    private val todoRepository: TodoRepository
) {


    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun init() {
        if (todoRepository.findAll().isEmpty()){
            todoRepository.saveAll(createDummyTodos(100))
        }
    }


    fun createDummyTodos(size: Int): MutableList<Todo> {
        val todos = mutableListOf<Todo>()
        for (i:Int in 1.. size) {
            val todoSaveDto = TodoSaveDto(
                content = "todo $i",
                status = Todo.Status.NORMAL
            )
            todos.add(todoSaveDto.toEntity())
        }
        return todos
    }

    @Transactional(readOnly = true)
    fun findTodosByPage(pageable: Pageable): Page<TodoResponseDto> {
        return todoRepository.findTodosWithPage(pageable).map { TodoResponseDto.fromEntity(it) }
    }

    @Transactional
    fun save(todo: TodoSaveDto): TodoResponseDto {

        val save = todoRepository.save(todo.toEntity())

        return TodoResponseDto.fromEntity(save)
    }

    @Transactional
    fun deleteTodoById(id: Long) {
        todoRepository.deleteById(id)
    }

    @Transactional
    fun updateTodo(dto: TodoUpdateDto): TodoResponseDto {

        val todo =
            todoRepository.findById(dto.id).orElseThrow {
                throw EntityNotFoundException("cant find todo by id")
            }

        todo.update(dto.status, dto.content, dto.isFinish)

        return TodoResponseDto.fromEntity(todo)
    }


    @Transactional(readOnly = true)
    fun findTodoById(id: Long): TodoResponseDto {

        val todo =
            todoRepository.findByIdOrNull(id) ?: throw EntityNotFoundException("cant find todo by id ")

        return TodoResponseDto.fromEntity(todo)
    }


}
