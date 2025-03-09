package freeapp.me.springpytorch.service


import ai.djl.Application
import ai.djl.modality.Classifications
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.transform.CenterCrop
import ai.djl.modality.cv.transform.Normalize
import ai.djl.modality.cv.transform.Resize
import ai.djl.modality.cv.transform.ToTensor
import ai.djl.modality.cv.translator.ImageClassificationTranslator
import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDList
import ai.djl.repository.zoo.Criteria
import ai.djl.repository.zoo.ModelZoo
import ai.djl.repository.zoo.ZooModel
import ai.djl.training.util.ProgressBar
import ai.djl.translate.Pipeline
import ai.djl.translate.TranslatorContext
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Paths


@Service
class PyTorchService(
    private val mapper: ObjectMapper
) {

    val model: ZooModel<Image, Classifications> = loadModel()

    fun loadModel(): ZooModel<Image, Classifications> {

        val modelPath = Paths.get("traced_resnet_model.pt")

        println(modelPath.toAbsolutePath())

        val criteria = Criteria.builder()
            .setTypes(Image::class.java, Classifications::class.java)
            .optApplication(Application.CV.IMAGE_CLASSIFICATION)
            .optModelPath(modelPath) // path to the PyTorch model file
            .optTranslator(SimpleTranslator())
            //.optTranslator(builtInTranslator)
            .optProgress(ProgressBar())
            .build()

        return ModelZoo.loadModel(criteria)
    }


    fun classify(image: Image): Classifications {
        model.newPredictor().use { predictor ->
            val classifications = predictor.predict(image)

            val classNames : MutableList<String> = classifications.classNames
            val className :String = classifications.best<Classifications.Classification>().className

            val indexOf = classNames.indexOf(className)

            println("Predicted class name: $className")
            println("Predicted class index: $indexOf")


            return classifications
        }
    }

    private class SimpleTranslator : ai.djl.translate.Translator<Image, Classifications> {

        val json =
            ClassPathResource("/static/imagenet_class.json")
                .inputStream.readBytes().toString(StandardCharsets.UTF_8)

        override fun processOutput(ctx: TranslatorContext, list: NDList): Classifications {
            val probabilities = list.singletonOrThrow().softmax(0)


            val map = ObjectMapper().readValue(json, MutableMap::class.java)
            val labels = mutableListOf<String>()

            map.entries.map {
                val element =
                    it.value.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(",", "")
                labels.add(element)
            }

            val out = Classifications(labels, probabilities)
            return out
        }

        override fun processInput(ctx: TranslatorContext, input: Image): NDList {
            val array: NDArray =
                input.toNDArray(ctx.ndManager, Image.Flag.COLOR)

            val pipeline = Pipeline()
            pipeline
                .add(Resize(256))
                .add(CenterCrop(224,224))
                .add(ToTensor())
                .add(
                    Normalize(
                        floatArrayOf( 0.485f, 0.456f, 0.406f ),
                        floatArrayOf(0.229f, 0.224f, 0.225f )
                    )
                )
            //NDList(ctx.ndManager.create(floatArrayOf(3.0f)))
            return NDList(pipeline.transform(NDList(array)).singletonOrThrow())
        }


    }
}
