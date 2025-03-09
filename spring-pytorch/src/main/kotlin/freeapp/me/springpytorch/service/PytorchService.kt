package freeapp.me.springpytorch.service


import ai.djl.Application
import ai.djl.modality.Classifications
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.transform.CenterCrop
import ai.djl.modality.cv.transform.Normalize
import ai.djl.modality.cv.transform.Resize
import ai.djl.modality.cv.transform.ToTensor
import ai.djl.modality.cv.translator.ImageClassificationTranslator
import ai.djl.modality.cv.util.NDImageUtils
import ai.djl.ndarray.NDArray
import ai.djl.ndarray.NDList
import ai.djl.repository.zoo.Criteria
import ai.djl.repository.zoo.ModelZoo
import ai.djl.repository.zoo.ZooModel
import ai.djl.training.util.ProgressBar
import ai.djl.translate.Pipeline
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

        //https://s3.amazonaws.com/deep-learning-models/image-models/imagenet_class_index.json

        val modelPath = Paths.get("traced_resnet_model.pt")
        println(modelPath.toAbsolutePath())

        val builtInTranslator =
            ImageClassificationTranslator.builder()
                .addTransform(Resize(256))
                .addTransform(CenterCrop(224, 224))
                .addTransform(ToTensor())
                .optFlag(Image.Flag.COLOR)
                .addTransform(
                    Normalize(
                        floatArrayOf( 0.485f, 0.456f, 0.406f ),
                        floatArrayOf(0.229f, 0.224f, 0.225f )
                    )
                )
                .optApplySoftmax(true)
                .build()

        val criteria = Criteria.builder()
            .setTypes(Image::class.java, Classifications::class.java)
            .optApplication(Application.CV.IMAGE_CLASSIFICATION)
            .optModelPath(modelPath) // path to the PyTorch model file
            //.optTranslator(SimpleTranslator())
            .optTranslator(builtInTranslator)
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
            println(list.size)
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

            val pipeline = Pipeline()
            pipeline.add(Resize(256)).add(ToTensor())
            //NDList(ctx.ndManager.create(floatArrayOf(3.0f)))
            return NDList(pipeline.transform(NDList(array)).singletonOrThrow())
        }


    }
}
