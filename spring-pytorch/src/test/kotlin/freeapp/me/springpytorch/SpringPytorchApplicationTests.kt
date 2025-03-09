package freeapp.me.springpytorch

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.nio.charset.StandardCharsets


//@SpringBootTest
class SpringPytorchApplicationTests {

    @Test
    fun contextLoads() {
        //https://s3.amazonaws.com/deep-learning-models/image-models/imagenet_class_index.json
        val json =
            ClassPathResource("/static/imagenet_class.json")
                .inputStream.readBytes().toString(StandardCharsets.UTF_8)

        //println(json)
        val map = ObjectMapper().readValue(json, MutableMap::class.java)

        val labels = mutableListOf<String>()

        map.entries.map {
            //println(it)
            val element =
                it.value.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(",", "")
            labels.add(element)
        }

        println(labels)

    }

}
