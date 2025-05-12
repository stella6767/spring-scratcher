package freeapp.life.swaggerrestdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import freeapp.life.swaggerrestdoc.util.ApiDocumentationBase
import freeapp.life.swaggerrestdoc.web.dto.TodoResponseDto
import freeapp.life.swaggerrestdoc.web.dto.TodoSaveDto
import freeapp.life.swaggerrestdoc.web.dto.TodoUpdateDto
import org.instancio.Instancio
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
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
                writer.document(
                    queryParameters(
                        parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                        parameterWithName("size").description("페이지 크기").optional()
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("data.content[]").description("Todo 목록"),
                        fieldWithPath("data.content[].id").description("Todo ID"),
                        fieldWithPath("data.content[].content").description("Todo 내용"),
                        fieldWithPath("data.content[].status").description("Todo 상태"),
                        fieldWithPath("data.content[].isFinish").description("완료 여부"),
                        fieldWithPath("data.content[].createdAt").description("생성일"),
                        fieldWithPath("data.content[].updatedAt").description("최종 수정일"),
                        fieldWithPath("data.pageable").description("페이지 정보"),
                        fieldWithPath("data.pageable.pageNumber").description("현재 페이지 번호"),
                        fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                        fieldWithPath("data.pageable.sort").description("정렬 정보"),
                        fieldWithPath("data.pageable.offset").description("오프셋"),
                        fieldWithPath("data.pageable.paged").description("페이징 사용 여부"),
                        fieldWithPath("data.pageable.unpaged").description("페이징 미사용 여부"),
                        fieldWithPath("data.totalElements").description("전체 요소 수"),
                        fieldWithPath("data.totalPages").description("전체 페이지 수"),
                        fieldWithPath("data.last").description("마지막 페이지 여부"),
                        fieldWithPath("data.size").description("페이지 크기"),
                        fieldWithPath("data.number").description("현재 페이지 번호"),
                        fieldWithPath("data.sort").description("정렬 정보"),
                        fieldWithPath("data.sort.empty").description("정렬 정보 존재 여부"),
                        fieldWithPath("data.sort.sorted").description("정렬됨 여부"),
                        fieldWithPath("data.sort.unsorted").description("정렬 안됨 여부"),
                        fieldWithPath("data.numberOfElements").description("현재 페이지의 요소 수"),
                        fieldWithPath("data.first").description("첫 페이지 여부"),
                        fieldWithPath("data.empty").description("결과 비어있음 여부")
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
                writer.document(
                    pathParameters(
                        parameterWithName("id").description("Todo ID")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    )
                )
            )
    }

    @Test
    fun updateTodo() {
        // Given
        val id = 1L
        val uri = "/todo/{id}"

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
            RestDocumentationRequestBuilders.put(uri, id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                writer.document(
                    pathParameters(
                        parameterWithName("id").description("Todo ID")
                    ),
                    requestFields(
                        fieldWithPath("id").description("Todo ID"),
                        fieldWithPath("content").description("수정할 Todo 내용"),
                        fieldWithPath("status").description("수정할 Todo 상태"),
                        fieldWithPath("isFinish").description("완료 여부")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    )
                )
            )
    }

    @Test
    fun deleteTodoById() {
        // Given
        val id = 1L
        val uri = "/todo/todo/{id}"

        Mockito.doNothing().`when`(todoService).deleteTodoById(id)

        // When & Then
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.delete(uri, id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.ALL)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                writer.document(
                    pathParameters(
                        parameterWithName("id").description("삭제할 Todo ID")
                    ),
                    responseFields(
                        *commonResponseFields()
                    )
                )
            )
    }

    @Test
    fun saveTodo() {
        // Given
        val uri = "/todo/todo"

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
                writer.document(
                    requestFields(
                        fieldWithPath("content").description("Todo 내용"),
                        fieldWithPath("status").description("Todo 상태")
                    ),
                    responseFields(
                        *commonResponseFields(),
                        fieldWithPath("data.id").description("생성된 Todo ID"),
                        fieldWithPath("data.content").description("Todo 내용"),
                        fieldWithPath("data.status").description("Todo 상태"),
                        fieldWithPath("data.isFinish").description("완료 여부"),
                        fieldWithPath("data.createdAt").description("생성일"),
                        fieldWithPath("data.updatedAt").description("최종 수정일")
                    )
                )
            )
    }
}
