package freeapp.life.swaggerrestdoc.web

import freeapp.life.swaggerrestdoc.entity.Todo
import freeapp.life.swaggerrestdoc.service.TodoService
import freeapp.life.swaggerrestdoc.web.dto.SuccessResponse
import freeapp.life.swaggerrestdoc.web.dto.TodoResponseDto
import freeapp.life.swaggerrestdoc.web.dto.TodoSaveDto
import freeapp.life.swaggerrestdoc.web.dto.TodoUpdateDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*


@RequestMapping("/todo")
@RestController
class TodoController(
    private val todoService: TodoService,
) {


    @GetMapping("/list")
    fun findTodos(
        @PageableDefault(size = 10) pageable: Pageable
    ): SuccessResponse<Page<TodoResponseDto>> {

        return SuccessResponse(
            "find todos",
            todoService.findTodosByPage(pageable)
        )
    }

    @GetMapping("/{id}")
    fun findTodoById(
        @PathVariable id: Long
    ): SuccessResponse<TodoResponseDto> {

        return SuccessResponse(
            "find todos",
            todoService.findTodoById(id)
        )
    }


    @PutMapping("/{id}")
    fun updateTodo(
        @RequestBody updateDto: TodoUpdateDto
    ): SuccessResponse<TodoResponseDto> {

        val todo =
            todoService.updateTodo(updateDto)

        return SuccessResponse(
            "update todo",
            todo
        )
    }


    @DeleteMapping("/todo/{id}")
    fun deleteTodoById(@PathVariable id: Long): SuccessResponse<Unit> {

        return SuccessResponse(
            "delete todo by id",
            todoService.deleteTodoById(id)
        )
    }

    @PostMapping("/todo")
    fun saveTodo(
        @RequestBody saveDto: TodoSaveDto
    ): SuccessResponse<TodoResponseDto> {

        return SuccessResponse(
            "save todo",
            todoService.save(saveDto)
        )
    }


}
