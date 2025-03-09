package freeapp.me.springpytorch.service


import ai.djl.Application
import ai.djl.modality.Classifications
import ai.djl.modality.Classifications.Classification
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.util.NDImageUtils
import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDList
import ai.djl.repository.zoo.Criteria
import ai.djl.repository.zoo.ModelZoo
import ai.djl.repository.zoo.ZooModel
import ai.djl.training.util.ProgressBar
import ai.djl.translate.TranslatorContext
import org.springframework.stereotype.Service
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.stream.IntStream


@Service
class PyTorchService {


//    private val model: ZooModel<Image, Classifications> by lazy {
//        loadModel()
//    }

    val model: ZooModel<Image, Classifications> = loadModel()

    fun loadModel(): ZooModel<Image, Classifications> {

        val modelPath = Paths.get("traced_resnet_model.pt")

        println(modelPath.toAbsolutePath())

        val criteria = Criteria.builder()
            .setTypes(Image::class.java, Classifications::class.java)
            .optApplication(Application.CV.IMAGE_CLASSIFICATION)
            .optModelPath(modelPath) // path to the PyTorch model file
            .optTranslator(SimpleTranslator())
            .optProgress(ProgressBar())
            .build()

        return ModelZoo.loadModel(criteria)
    }


    fun classify(image: Image): Classifications {
        model.newPredictor().use { predictor ->
            val classifications = predictor.predict(image)
            return classifications
        }
    }

    private class SimpleTranslator : ai.djl.translate.Translator<Image, Classifications> {
        override fun processOutput(ctx: TranslatorContext, list: NDList): Classifications {

            val probabilities = list.singletonOrThrow().softmax(0)
            val classNames = IntStream.range(0, 510).mapToObj<String> { i: Int ->
                java.lang.String.valueOf(i)
            }.collect(Collectors.toList<String>())

//            val array = list[0]
//            val pred = array.softmax(-1)
//            val labels: MutableList<String> = ArrayList()
//            labels.add("benign")
//            labels.add("malicious")
            //Classifications(labels, pred)
            val out = Classifications(classNames, probabilities)
            return out
        }

        override fun processInput(ctx: TranslatorContext, input: Image): NDList {
            val array: NDArray =
                input.toNDArray(ctx.ndManager, Image.Flag.COLOR)
            return NDList(NDImageUtils.toTensor(array))
        }
    }
}
