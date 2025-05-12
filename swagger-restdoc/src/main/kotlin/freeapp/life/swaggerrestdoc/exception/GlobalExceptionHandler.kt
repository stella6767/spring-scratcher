package freeapp.life.swaggerrestdoc.exception




import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.LocalDateTime
import java.util.stream.Collectors


@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(this::class.java)

    // 모든 예외를 처리하는 기본 핸들러
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ProblemDetail {

        log.error(ex.message, ex)

        return createProblemDetail(
            ex,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Server Error",
            ex.localizedMessage,
            "https://api.example.com/problems/server-error",
        )
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ProblemDetail {

        log.error(ex.message, ex)

        return createProblemDetail(
            ex,
            HttpStatus.BAD_REQUEST,
            "Invalid Argument",
            ex.message ?: "Invalid argument provided",
            "https://api.example.com/problems/invalid-argument",
        )
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointerException(ex: NullPointerException, request: WebRequest): ProblemDetail {

        log.error(ex.message, ex)

        return createProblemDetail(
            ex,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Null Reference",
            ex.message ?: "A null reference was accessed",
            "https://api.example.com/problems/null-reference",
        )
    }



    // ConstraintViolationException 처리 (Bean Validation)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException, request: WebRequest): ProblemDetail {

        log.error(ex.message, ex)

        val message = ex.constraintViolations
            .stream()
            .map { violation -> "${violation.propertyPath}: ${violation.message}" }
            .collect(Collectors.joining(", "))

        val problemDetail = createProblemDetail(
            ex,
            HttpStatus.BAD_REQUEST,
            "Validation Error",
            "Validation failed: $message",
            "https://api.example.com/problems/validation-error",
        )

        // 추가 필드로 검증 오류 상세 정보 포함
        val errors = ex.constraintViolations.associate {
            it.propertyPath.toString() to it.message
        }

        problemDetail.setProperty("errors", errors)

        return problemDetail
    }




    // Spring 내장 예외에 대한 처리를 커스터마이징할 때는 이 메서드를 오버라이드
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: org.springframework.http.HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {

        log.error(ex.message, ex)

        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val httpStatus = HttpStatus.BAD_REQUEST

        val problemDetail = ProblemDetail.forStatusAndDetail(
            httpStatus,
            "Validation failed for request parameters"
        )
        problemDetail.title = "Validation Error"
        problemDetail.type = URI.create("https://api.example.com/problems/validation-error")
        problemDetail.setProperty("errors", errors)
        return ResponseEntity
            .status(httpStatus)
            .contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON)
            .body(problemDetail)
    }

    companion object {

        // 공통 ProblemDetail 생성 헬퍼 메서드
        fun createProblemDetail(
            ex: Exception,
            status: HttpStatus,
            title: String,
            detail: String,
            type: String = "",
        ): ProblemDetail {

            val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
            problemDetail.title = title
            problemDetail.type = URI.create(type)

            // 추가 필드
            problemDetail.setProperty("exception", ex.javaClass.simpleName)

            return problemDetail
        }

    }

}
