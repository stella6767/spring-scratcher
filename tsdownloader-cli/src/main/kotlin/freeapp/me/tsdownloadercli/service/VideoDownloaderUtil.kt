package freeapp.me.tsdownloadercli.service


import freeapp.me.tsdownloadercli.util.DownloaderTask
import freeapp.me.tsdownloadercli.util.ResultVO
import mu.KotlinLogging
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern


class VideoDownloaderUtil() {

    private val log = KotlinLogging.logger {  }

    fun downloaderUtil(m3u8FileURL: String, downloadDirectory:String): File {

        // Download m3u8 description file and return a list of its .ts url contents
        var m3u8TsFilesList = this.processM3U8urlFile(m3u8FileURL)

        if (m3u8TsFilesList.isEmpty()) {
            log.error("ERROR!!!ERROR!!! - No TS files... Exiting >>>$m3u8FileURL")
            throw IllegalArgumentException("No TS files... Exiting >>>$m3u8FileURL")

        } else {
            // Check if the extracted urls are relative or absolute paths
            val tsVideoUrl = m3u8TsFilesList.first()
            if (!tsVideoUrl.startsWith("http:") && !tsVideoUrl.startsWith("https:")) {
                log.warn(
                    "WARN! WARN! The following file location is not an absolute path URL!\n" +
                            " Current relative path = " + tsVideoUrl + " Absolute path will be based on current on the M3U8 file path: " + m3u8FileURL
                )
                val absoluteM3u8TsFilesList = mutableListOf<String>()
                for (relativeTSUrls: String in m3u8TsFilesList) {
                    // Using M3U8 url to construct absolute paths for ts file urls
                    val absoluteTS = getAbsolutePath(m3u8FileURL, relativeTSUrls)
                    absoluteM3u8TsFilesList.add(absoluteTS)
                }
                log.info("New absolute file path url" + absoluteM3u8TsFilesList.first())
                m3u8TsFilesList = absoluteM3u8TsFilesList
            }
        }

        filesToDownloadCount = m3u8TsFilesList.size

        // Video files will be downloaded to  this directory
        println("\nWARNING!! WARNING!! Will overwrite existing Full_Video.ts\n")
        println("Download videos to directory >> $downloadDirectory")


        val tsLinkVsPathHashmap: HashMap<String, String> = hashMapOf()

        for (tsLink: String in m3u8TsFilesList) {

            //val p = Pattern.compile("[^\\/]+(?=\\.ts).ts") // the pattern to search for
            val p = Pattern.compile("[^\\s]+\\.(ts|m4s|mp4)", Pattern.MULTILINE)
            val m = p.matcher(tsLink)

//            val videoName = if (m.find()) {
//                m.group(0)
//            } else ""
//            val tsFilePath = downloadDirectory + videoName

            val videoName = FilenameUtils.getName(tsLink)
            val tsFilePath = Paths.get(downloadDirectory, videoName).toString()

            tsLinkVsPathHashmap[tsLink] = tsFilePath
            log.info("Ts file path appended: $tsFilePath")
        }
        // Add last or first part of builder depending on builder string length


        // Download and save all 'ts' files. 'NULL' is returned if ALL files were successfully downloaded.
        // Else, failed files will be retried.
        val resultVOS =
            downloadAndStoreFiles(m3u8TsFilesList, tsLinkVsPathHashmap)

        val successDownloadsList = resultVOS.filter { it.isSuccess }

        // Failed files will be retired 3 times.
        if (resultVOS.all { it.isSuccess }) {
            println("All files successfully downloaded no need to retry any! :)")
        } else {

            var retryTaskList = getDownloaderTaskRetryList(resultVOS)
            var retryCounter = 0

            log.error("ERROR!!! ERROR!!! Some files were NOT successfully downloaded. Will retry for " + retryTaskList.size + " file(s)")

            do {
                val newResultVOS =
                    downloadAndStoreFiles(m3u8TsFilesList, mapOf(), retryTaskList)
                successDownloadsList.union(newResultVOS.filter { it.isSuccess })
                if (newResultVOS.any { !it.isSuccess }) {
                    retryTaskList = getDownloaderTaskRetryList(newResultVOS)
                }
                retryCounter++

            } while ((newResultVOS.any { !it.isSuccess }) && retryCounter != 3)

            if (retryCounter == 3) {
                log.error(
                    "ERROR!!! ERROR!!! ERROR!!!! - After more than 3 attempts some files did NOT successfully download. \n" +
                            "ERROR!!! ERROR!!! ERROR!!!! - Your video may NOT yield the expected result or ffmpeg tool can fail"
                )
            }
        }


        val combineFile = java.io.File(downloadDirectory + "list.txt")

        combineFile.bufferedWriter().use { writer ->
            for (result in successDownloadsList) {
                if (result.isSuccess) {
                    val name = FilenameUtils.getBaseName(result.tsFileAbsoluteUrl)

                    println("name::$name")

                    val commandStr = arrayOf(
                        "ffmpeg",
                        "-i",
                        result.tsFileAbsoluteUrl,
                        "-c",
                        "copy",
                        "-bsf:v",
                        "h264_mp4toannexb",
                        "${name}.ts"
                    ).joinToString(" ")

                    executeFFMPEG2(commandStr)

                    writer.write("file " + "'${name}.ts'")
                    writer.newLine()
                }
            }
        }

        // Prepare and execute ts video concatenation
        val outputFilePath =
            downloadDirectory + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "_" + FilenameUtils.getBaseName(m3u8FileURL) + ".mp4"

        executeFFMPEG(outputFilePath, combineFile)


        println("Full output video name = $outputFilePath")
        //println("================== Full Video @ $fullVideo ==================")
        println("\n================== Video download finished for $m3u8FileURL ==================")


        for (result in successDownloadsList) {
            val currentFile = java.io.File(result.tsFileAbsoluteUrl)
            log.info("Deleting file " + currentFile.absolutePath)
            if (!currentFile.delete()) {
                log.warn("Failed to delete file ${result.tsFileAbsoluteUrl}")
            }
        }

//        combineFile.delete()

        // reset counter
        totalDownloadCounter.getAndAdd(downloadCounter.decrementAndGet())
        downloadCounter.set(1)

        return File(outputFilePath)
    }

    private fun executeFFMPEG(
        outputFilePath: String,
        file: java.io.File
    ) {
        //https://trac.ffmpeg.org/wiki/Concatenate
        println("Executing ffmpeg with output path = $outputFilePath\n")

        // Build ffmpeg tool command and execute
        // ffmpeg will concat all our *.ts files and produce a single output video file
        val processBuilder = ProcessBuilder()
        //processBuilder.directory(java.io.File(System.getProperty("user.home")))


        //"ffmpeg -f concat -safe 0 -i list.txt -c copy output.mp4"

        //ffmpeg -f concat -safe 0 -analyzeduration 100M -probesize 100M -i list.txt -c copy output.mp4

        val commandStr = arrayOf(
            "ffmpeg",
            "-f",
            "concat",
            "-safe",
            "0",
            "-i",
            file.canonicalPath,
            "-c",
            "copy",
            "-bsf:a",
            "aac_adtstoasc",
            outputFilePath
        ).joinToString(" ")

        // Run this on Windows, cmd, /c = terminate after this run
        //processBuilder.command(commandStr)
        processBuilder.command("sh", "-c", commandStr)

        processBuilder.redirectErrorStream(true)

        println("Executing command - $commandStr")
        val process = processBuilder.start()
        // Let's read and print the ffmpeg's output
        val r = BufferedReader(InputStreamReader(process.inputStream))
        while (true) {
            val line = r.readLine() ?: break
            println(line)
        }

    }

    fun executeFFMPEG2(
        commandStr: String,
    ) {

        println("test!!!")

        val processBuilder = ProcessBuilder()
        // Run this on Windows, cmd, /c = terminate after this run
        //processBuilder.command(commandStr)
        processBuilder.command("sh", "-c", commandStr)
        processBuilder.redirectErrorStream(true)
        println("Executing command - $commandStr")
        val process = processBuilder.start()
        // Let's read and print the ffmpeg's output
        val r = BufferedReader(InputStreamReader(process.inputStream))
        while (true) {
            val line = r.readLine() ?: break
            println(line)
        }
    }

    /*
     * Download *.m3u8 file.
     * Find and get all *.ts file names in m3u8 file (These should be listed in sequential order)
     * Return list of *.ts file urls
     */

    private fun processM3U8urlFile(m3u8Url: String): List<String> {

        val url = URI(m3u8Url).toURL()
        val m3u8List = mutableListOf<String>()

        log.info("한글 포함 url 403 issue 있음. 아직 해결 못함")
        val pathSegments =
            url.toString().split(File.separator).toMutableList()
        println(pathSegments.last())

        val encodedFileName =
            URLEncoder.encode(pathSegments.last(), StandardCharsets.UTF_8.toString())

        pathSegments[pathSegments.size - 1] = encodedFileName

        val newPath = pathSegments.joinToString(File.separator)
        val fullURL = URI(newPath).toURL()

        println(fullURL.toString())

        val httpCon = fullURL.openConnection() as HttpURLConnection

        httpCon.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36"
        )
//        httpCon.addRequestProperty("Referer", "https://creative.rmishe.com/")
//        httpCon.addRequestProperty("authority", "b-hls-23.sacdnssedge.com")
//        httpCon.addRequestProperty("accept-language", "en-US,en;q=0.9")
//        httpCon.addRequestProperty("Origin", "https://creative.rmishe.com")
//        httpCon.addRequestProperty("if-modified-since", "Fri, 14 Feb 2025 16:25:10 GMT")
//        httpCon.addRequestProperty("sec-fetch-site", "cross-site")
//        httpCon.addRequestProperty("sec-fetch-Mode", "cors")
//        httpCon.addRequestProperty("sec-fetch-Dest", "empty")

        httpCon.connect()

        val inputStream = httpCon.inputStream

        inputStream.use { `is` ->
            val m3u8File: String = IOUtils.toString(`is`, StandardCharsets.UTF_8)

            println(m3u8File)

            // Find and get all *.ts file names in m3u8 file
            val m =
                Pattern.compile("https://[^\\s]+\\.(ts|m4s|mp4)", Pattern.MULTILINE).matcher(m3u8File)
            while (m.find()) {
                val tsVideoUrl = m.group()
                m3u8List.add(tsVideoUrl)
            }
        }

        println("Total ts files to be downloaded ${m3u8List.size}")
        return m3u8List
    }

    /*
     * Divide and conquer the video file downloads
     * Method also handles download retries if the 'retryTaskList' is not empty/null
     */
    private fun downloadAndStoreFiles(
        m3u8List: List<String>,
        linkVsFilePath: Map<String, String>,
        retryTaskList: MutableList<DownloaderTask> = mutableListOf()
    ): List<ResultVO> {

        val executor = Executors.newVirtualThreadPerTaskExecutor()

        // Check if this is a retry download task
        val taskList = if (retryTaskList.isNotEmpty()) {
            retryTaskList
        } else {
            val newTaskList = mutableListOf<DownloaderTask>()
            for (tsUrlLink in m3u8List) {
                val tsFilePath = linkVsFilePath[tsUrlLink].toString()
                val element = DownloaderTask(tsUrlLink, tsFilePath)
                newTaskList.add(element)
            }
            newTaskList
        }

        // Execute all downloader tasks and get reference to Future objects
        val results =
            executor.invokeAll(taskList).map { it.get() }

        val failures = results.filter { !it.isSuccess }
        println("===================== A total of " + failures.size + " failed to download =====================")

        return results
    }

    /*
     * Create and get the Retry tasks for previously unsuccessful downloads
     */
    private fun getDownloaderTaskRetryList(resultVos: List<ResultVO>): MutableList<DownloaderTask> {
        println("===================== Retrying the following file links =====================")
        var fileCounter = 1

        return resultVos.filter { !it.isSuccess }.map {
            println(fileCounter.toString() + ". " + it.tsFileUrl)
            fileCounter++
            DownloaderTask(it.tsFileUrl, it.tsFilePath)
        }.toMutableList()
    }


    /*
     * Master/Playlist files can be identified if they contain the "EXT-X-STREAM-INF" tag
     */

    fun findMasterM3U(m3u8files: List<String>): String {

        println("Inspecting list of HAR m3u8 files to identify mater playlist")

        for ((counter, m3uFileUrl: String) in m3u8files.withIndex()) {
            
            println(m3uFileUrl)
            
            var url =
                URI(m3uFileUrl).toURL()

            val connection = url.openConnection() as HttpURLConnection

//            connection.addRequestProperty("User-Agent", "Mozilla")
//            connection.addRequestProperty("Referer", "https://creative.mnaspm.com/")
//            connection.addRequestProperty("authority", "edge-hls.sacdnssedge.com")
            connection.connect()

            println(connection.responseCode)

            connection.inputStream.use { `is` ->
                val playlistFileIO = IOUtils.toString(`is`, StandardCharsets.UTF_8)
                println("\n$counter. Printing content of m3u8 file\n$playlistFileIO")
                if (playlistFileIO.contains("EXT-X-STREAM-INF")) {
                    println("FOUND Master/Playlist m3u8 url: $m3uFileUrl")
                    return m3uFileUrl
                }
            }
        }

        println("WARNING! No m3u8 master playlist found!")
        return ""
    }

    /*
     * Multiple M3U8 file variants exist so find the highest quality one.
     */

    fun getM3U8variantWithHighestBitrate(masterPlaylistUrl: String): String {

        println("Finding highest bitrate in Master/Playlist file URL >>> $masterPlaylistUrl ")

        val url = URI(masterPlaylistUrl).toURL()

        var highestBitRateM3U8url = url.openStream().use { `is` ->

            val playlistFileIO: String = IOUtils.toString(`is`, StandardCharsets.UTF_8)
            println("Printing content of master playlist file >>> $playlistFileIO")

            // Find all bit rate variants
            val bitrateMatcher =
                Pattern.compile("[^AVERAGE-]BANDWIDTH=([0-9]+)", Pattern.MULTILINE).matcher(playlistFileIO)
            // Find and get all *.m3u8 file paths
            val m3u8Matcher =
                Pattern.compile("^.*\\.m3u8.*", Pattern.MULTILINE).matcher(playlistFileIO)

            var highestBitRate = 0
            var highestBitRateIdx = 0
            var idxCounter = 0

            println("Finding highest bitrate...")

            while (bitrateMatcher.find()) {

                val tempBitRate =
                    bitrateMatcher.group(1).toInt()

                println("tempBitRate - $tempBitRate")

                if (highestBitRate < tempBitRate) {
                    highestBitRate = tempBitRate
                    highestBitRateIdx = idxCounter
                    println("New highestBitRate - $highestBitRate at index - $highestBitRateIdx")
                }

                idxCounter++
            }

            val m3u8MatcherList: MutableList<String> = ArrayList()
            while (m3u8Matcher.find()) {
                println("m3u8Matcher - " + m3u8Matcher.group())
                m3u8MatcherList.add(m3u8Matcher.group())
            }
            println("Highest bit rate m3u8 match - ${m3u8MatcherList[highestBitRateIdx]}")

            m3u8MatcherList[highestBitRateIdx]
        }


        if (!highestBitRateM3U8url.startsWith("http:") && !highestBitRateM3U8url.startsWith("https:")) {

            println(
                ("WARN! WARN! The following file location is not an absolute path URL!\n" +
                        " Current relative path = " + highestBitRateM3U8url + " Absolute path will be based on current on the M3U8 file path: " + masterPlaylistUrl)
            )
            // Using M3U8 url to construct absolute paths for ts file urls
            highestBitRateM3U8url = getAbsolutePath(masterPlaylistUrl, highestBitRateM3U8url)
            println("New absolute file path url=>$highestBitRateM3U8url")
        }

        return highestBitRateM3U8url
    }


    private fun getAbsolutePath(baseAbsolutePath: String, relativePath: String?): String {
        val idx = baseAbsolutePath.lastIndexOf(File.separator)
        val base = baseAbsolutePath.substring(0, idx + 1)
        val absolutePath = base + relativePath
        return absolutePath
    }


    companion object {
        var downloadCounter: AtomicInteger = AtomicInteger(1)
        var totalDownloadCounter: AtomicInteger = AtomicInteger(0)
        var filesToDownloadCount: Int = 0
    }
}
