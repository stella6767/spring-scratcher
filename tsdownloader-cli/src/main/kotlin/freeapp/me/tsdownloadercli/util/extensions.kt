import org.springframework.core.io.ClassPathResource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

fun ClassPathResource.getText(): String {
    return this.inputStream.readBytes().toString(StandardCharsets.UTF_8)
}
