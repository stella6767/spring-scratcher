package freeapp.me.tsdownloadercli.service


import freeapp.me.tsdownloadercli.util.DownloaderTask
import freeapp.me.tsdownloadercli.util.M3u8RequestDto
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

    private val log = KotlinLogging.logger { }

    fun downloaderUtil(requestDto: M3u8RequestDto, downloadDirectory: String): File {

        // Download m3u8 description file and return a list of its .ts url contents
        val m3u8FileURL = requestDto.m3u8file
        var m3u8TsFilesList = this.processM3U8urlFile(requestDto)
        if (m3u8TsFilesList.isEmpty()) {
            log.error("ERROR!!!ERROR!!! - No TS files... Exiting >>>$m3u8FileURL")
            throw IllegalArgumentException("No TS files... Exiting >>>$m3u8FileURL")
        }

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
            downloadDirectory + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 8) + "_" + FilenameUtils.getBaseName(m3u8FileURL) + ".mp4"

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

    /*
     * Download *.m3u8 file.
     * Find and get all *.ts file names in m3u8 file (These should be listed in sequential order)
     * Return list of *.ts file urls
     */

    fun processM3U8urlFile(request: M3u8RequestDto): List<String> {

        val url = URI(request.m3u8file).toURL()
        val m3u8List = mutableListOf<String>()
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

        for (header in request.reqHeaders) {
            if (header.name.contains("origin") ||
                header.name.contains("referer") ||
                header.name.contains("user-agent") ||
                header.name.contains("authority")
            ) {
                val key = header.name.replace(":", "")
                val value = header.value.replace(":", "")

                println("key==>$key  value==>$value")

                httpCon.addRequestProperty(key, value)
            }
        }

        //httpCon.addRequestProperty("origin", "https://hclips.com")


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

            if (m3u8MatcherList.isEmpty()) {
                throw IllegalArgumentException("ERROR!!! Failed while reading m3u8MatcherList")
            } else {
                println("Highest bit rate m3u8 match - ${m3u8MatcherList[highestBitRateIdx]}")
                m3u8MatcherList[highestBitRateIdx]
            }
            m3u8MatcherList[highestBitRateIdx]
        }


//        if (!highestBitRateM3U8url.startsWith("http:") && !highestBitRateM3U8url.startsWith("https:")) {
//            println(
//                ("WARN! WARN! The following file location is not an absolute path URL!\n" +
//                        " Current relative path = " + highestBitRateM3U8url + " Absolute path will be based on current on the M3U8 file path: " + masterPlaylistUrl)
//            )
//            // Using M3U8 url to construct absolute paths for ts file urls
//            highestBitRateM3U8url = getAbsolutePath(masterPlaylistUrl, highestBitRateM3U8url)
//            println("New absolute file path url=>$highestBitRateM3U8url")
//        }

        return highestBitRateM3U8url
    }


    /*
     * Master/Playlist files can be identified if they contain the "EXT-X-STREAM-INF" tag
     */

    fun findMasterM3U(requests: MutableList<M3u8RequestDto>): M3u8RequestDto {

        println("Inspecting list of HAR m3u8 files to identify mater playlist")

        for ((counter, req) in requests.withIndex()) {
            var url = URI(req.m3u8file).toURL()
            val connection = url.openConnection() as HttpURLConnection
            for (header in req.reqHeaders) {
                println("${header.name} :: ${header.value}")
                if (header.name.contains("origin") ||
                    header.name.contains("referer") ||
                    header.name.contains("user-agent")
                ) {
                    connection.addRequestProperty(header.name.replace(":", ""), header.value.replace(":", ""))
                }
            }
            connection.connect()
            println(connection.responseCode)
            val playlistFileIO = connection.inputStream.use { `is` ->
                val playlistFileIO = IOUtils.toString(`is`, StandardCharsets.UTF_8)
                println("\n$counter. Printing content of m3u8 file\n$playlistFileIO")
                playlistFileIO
            }
            if (playlistFileIO.contains("EXT-X-STREAM-INF")) {
                println("FOUND Master/Playlist m3u8 url: ${req.m3u8file}")

                val highestBitrateUrl =
                    getM3U8variantWithHighestBitrate(req.m3u8file)
                req.m3u8file = highestBitrateUrl
                return req
            }
        }
        println("WARNING! No m3u8 master playlist found!")
        return requests[0]
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
