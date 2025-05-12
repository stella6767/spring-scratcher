package freeapp.life.swaggerrestdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.life.swaggerrestdoc.entity.Todo
import freeapp.life.swaggerrestdoc.util.ApiDocumentationBase
import freeapp.life.swaggerrestdoc.web.dto.TodoResponseDto
import freeapp.life.swaggerrestdoc.web.dto.TodoSaveDto
import freeapp.life.swaggerrestdoc.web.dto.TodoUpdateDto
import org.instancio.Instancio
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


class TodoControllerTest : ApiDocumentationBase() {

    @Test
    fun findTodos() {

        val uri = "/todo/list"

        // 더미 TaskResponseDto 리스트 생성
        val size = 10
        val page = 0


        val content = Instancio.ofList(TodoResponseDto::class.java)
            .size(size)
            .create()

        // Page 객체 생성
        val pageRequest = PageRequest.of(page, size)
        val totalElements = 100L
        val todos = PageImpl(content, pageRequest, totalElements)


        Mockito
            .`when`(todoService.findTodosByPage(PageRequest.of(page, size)))
            .thenReturn(todos)


        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(
                uri
            )
                .param("page", page.toString())
                .param("size", size.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)

        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                documentApi(
                    identifier = "todo-list",
                    tag = "Todo API",
                    summary = "Todo 목록 조회 API",
                    queryParams = getPageableParameters(),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        *getPageableResponseFields(),
                        fieldWithPath("data.content[]").description("Todo 목록"),
                        fieldWithPath("data.content[].id").description("Todo ID"),
                        fieldWithPath("data.content[].content").description("Todo 내용"),
                        fieldWithPath("data.content[].status").description("Todo 상태"),
                        fieldWithPath("data.content[].isFinish").description("완료 여부"),
                        fieldWithPath("data.content[].createdAt").description("생성일"),
                        fieldWithPath("data.content[].updatedAt").description("최종 수정일"),
                    ),

                    )
            )
    }


    @Test
    fun findTodoById() {
        // Given
        val id = 1L
        val uri = "/todo/{id}"

        val todoDto = Instancio.of(TodoResponseDto::class.java)
            .create()

        Mockito
            .`when`(todoService.findTodoById(id))
            .thenReturn(todoDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                documentApi(
                    identifier = "todo-find-by-id",
                    tag = "Todo API",
                    summary = "Todo 상세 조회 API",
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    ),
                    pathParams = arrayOf(
                        parameterWithName("id").description("Todo ID")
                    ),

                    )
            )
    }

    @Test
    fun updateTodo() {
        // Given
        val uri = "/todo"

        val updateDto = Instancio.of(TodoUpdateDto::class.java)
            .create()

        val responseDto = Instancio.of(TodoResponseDto::class.java)
            .create()

        Mockito
            .`when`(todoService.updateTodo(updateDto))
            .thenReturn(responseDto)

        // JSON 문자열로 변환
        val content = objectMapper.writeValueAsString(updateDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                documentApi(
                    identifier = "update todo",
                    tag = "Todo API",
                    summary = "Todo update API",
                    requestFields = arrayOf(
                        fieldWithPath("id").description("Todo ID"),
                        fieldWithPath("content").description("수정할 Todo 내용"),
                        fieldWithPath("status").description("수정할 Todo 상태"),
                        fieldWithPath("isFinish").description("완료 여부")
                    ),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    ),


                    )
            )
    }

    @Test
    fun deleteTodoById() {
        // Given
        val id = 1L
        val uri = "/todo/{id}"

        Mockito.doNothing().`when`(todoService).deleteTodoById(id)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.delete(uri, id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                documentApi(
                    identifier = "delete-todo-by-id",
                    tag = "Todo API",
                    summary = "Todo 삭제 API",
                    responseFields = arrayOf(
                        *commonResponseFields(),
                    ),
                    pathParams = arrayOf(
                        parameterWithName("id").description("Todo ID")
                    ),

                    )
            )
    }

    @Test
    fun saveTodo() {
        // Given
        val uri = "/todo"

        val saveDto = Instancio.of(TodoSaveDto::class.java)
            .create()

        val responseDto = Instancio.of(TodoResponseDto::class.java)
            .create()

        Mockito
            .`when`(todoService.save(saveDto))
            .thenReturn(responseDto)

        // JSON 문자열로 변환
        val objectMapper = ObjectMapper()
        val content = objectMapper.writeValueAsString(saveDto)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                documentApi(
                    identifier = "todo-save",
                    tag = "Todo API",
                    summary = "Todo 생성 API",
                    requestFields = arrayOf(
                        fieldWithPath("content").description("Todo 내용"),
                        fieldWithPath("status").description("Todo 상태 => ${getEnumValues(Todo.Status::class.java)}")
                    ),
                    responseFields = arrayOf(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    ),


                    )

            )
    }
}
