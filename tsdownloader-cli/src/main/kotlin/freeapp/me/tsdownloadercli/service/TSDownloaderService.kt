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
    private val playWriterService = PlayWriterService()

    fun downloadTsByUrl(url: String): File {


        // 1. m3u8 file 찾기

        val m3U8requestFiles =
            playWriterService.retrieveM3U8requestFiles(url)

        val outputPath =
            Paths.get(".").toAbsolutePath().toUri().normalize().rawPath + "output/"

        log.info(outputPath)

        if (m3U8requestFiles.isEmpty()) {
            throw IllegalArgumentException(
                "ERROR! No http requests for m3u8 files were found while searching website --> " + m3U8requestFiles +
                        "\nPlease provide the direct m3u8 URL."
            )
        }


        //2.

        val masterM3U =
            videoDownloaderUtil.findMasterM3U(m3U8requestFiles)


        val directM3U8fileURL = if (!StringUtils.hasLength(masterM3U)) {
            log.info(
                "WARNING!! WARNING!! Did not find a master playlist\n" +
                        "The first m3u8 url file found will be used to attempt video download --> " + m3U8requestFiles
            )
            m3U8requestFiles.first()
        } else {
            val highestBitRateVariant = videoDownloaderUtil.getM3U8variantWithHighestBitrate(masterM3U)
            log.info("FOUND HIGHEST BIT RATE URL = $highestBitRateVariant")
            highestBitRateVariant
        }

        if (!StringUtils.hasLength(directM3U8fileURL)) {
            throw  IllegalArgumentException("ERROR!!!ERROR!!! No M3U8 file url was provide/found")
        }

        val downloadedFile =
            videoDownloaderUtil.downloaderUtil(directM3U8fileURL, outputPath)

        return downloadedFile
    }





}
