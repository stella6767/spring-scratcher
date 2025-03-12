package freeapp.me.tsdownloadercli.service


import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.io.File
import java.nio.file.Paths


@Service
class TSDownloaderService {

    private val log = KotlinLogging.logger {  }
    private val videoDownloaderUtil = VideoDownloaderUtil()

    fun downloadTsByMasterM3u8Url(masterM3U: String) {

//        val directM3U8fileURL = if (!StringUtils.hasLength(masterM3U)) {
//            log.info(
//                "WARNING!! WARNING!! Did not find a master playlist\n" +
//                        "The first m3u8 url file found will be used to attempt video download --> " + masterM3U
//            )
//            masterM3U
//        } else {
//            val highestBitRateVariant = videoDownloaderUtil.getM3U8variantWithHighestBitrate(masterM3U)
//            log.info("FOUND HIGHEST BIT RATE URL = $highestBitRateVariant")
//            highestBitRateVariant
//        }
//
//        if (!StringUtils.hasLength(directM3U8fileURL)) {
//            throw  IllegalArgumentException("ERROR!!!ERROR!!! No M3U8 file url was provide/found")
//        }
//
//        val outputPath =
//            Paths.get(".").toAbsolutePath().toUri().normalize().rawPath + "output/"
//
//        val downloadedFile =
//            videoDownloaderUtil.downloaderUtil(directM3U8fileURL, outputPath)


//        return downloadedFile

        TODO()
    }





}
